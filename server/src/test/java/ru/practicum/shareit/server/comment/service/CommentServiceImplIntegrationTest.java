package ru.practicum.shareit.server.comment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.comment.dto.CommentDto;
import ru.practicum.shareit.server.comment.model.Comment;
import ru.practicum.shareit.server.comment.repository.CommentRepository;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.repository.ItemRepository;
import ru.practicum.shareit.server.user.entity.User;
import ru.practicum.shareit.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
class CommentServiceImplIntegrationTest {

    @Autowired
    private CommentServiceImpl commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private User author;
    private Item item1;
    private Item item2;
    private Comment comment1;
    private Comment comment2;
    private Comment comment3;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        // Используем усеченное время для согласованности с БД
        now = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);

        commentRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        owner = userRepository.save(owner);

        author = new User();
        author.setName("Author");
        author.setEmail("author@example.com");
        author = userRepository.save(author);

        item1 = new Item();
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setOwner(owner);
        item1 = itemRepository.save(item1);

        item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(true);
        item2.setOwner(owner);
        item2 = itemRepository.save(item2);

        comment1 = new Comment();
        comment1.setText("Great item!");
        comment1.setItem(item1);
        comment1.setAuthor(author);
        comment1.setCreated(now.minusDays(2));

        comment2 = new Comment();
        comment2.setText("Very useful, thanks!");
        comment2.setItem(item1);
        comment2.setAuthor(author);
        comment2.setCreated(now.minusDays(1));

        comment3 = new Comment();
        comment3.setText("Not bad");
        comment3.setItem(item2);
        comment3.setAuthor(author);
        comment3.setCreated(now);

        commentRepository.saveAll(List.of(comment1, comment2, comment3));
    }

    @Test
    void getCommentsForItem_ShouldReturnCommentsForSpecificItem() {
        List<CommentDto> result = commentService.getCommentsForItem(item1.getId());

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting("text")
                .containsExactly("Very useful, thanks!", "Great item!");

        assertThat(result)
                .extracting("authorName")
                .containsOnly(author.getName());

        assertThat(result)
                .extracting("itemId")
                .containsOnly(item1.getId());
    }

    @Test
    void getCommentsForItem_WhenNoComments_ShouldReturnEmptyList() {
        Item item3 = new Item();
        item3.setName("Item 3");
        item3.setDescription("Description 3");
        item3.setAvailable(true);
        item3.setOwner(owner);
        item3 = itemRepository.save(item3);

        List<CommentDto> result = commentService.getCommentsForItem(item3.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void getCommentsForItems_ShouldReturnCommentsForMultipleItems() {
        Map<Long, List<CommentDto>> result = commentService.getCommentsForItems(
                List.of(item1.getId(), item2.getId()));

        assertThat(result).hasSize(2);

        assertThat(result.get(item1.getId())).hasSize(2);
        assertThat(result.get(item1.getId()))
                .extracting("text")
                .containsExactly("Very useful, thanks!", "Great item!");

        assertThat(result.get(item2.getId())).hasSize(1);
        assertThat(result.get(item2.getId()).get(0).text()).isEqualTo("Not bad");
    }

    @Test
    void getCommentsForItems_WhenEmptyList_ShouldReturnEmptyMap() {
        Map<Long, List<CommentDto>> result = commentService.getCommentsForItems(List.of());

        assertThat(result).isEmpty();
    }

    @Test
    void getCommentsForItems_WhenItemsHaveNoComments_ShouldReturnEmptyLists() {
        Item item3 = new Item();
        item3.setName("Item 3");
        item3.setDescription("Description 3");
        item3.setAvailable(true);
        item3.setOwner(owner);
        item3 = itemRepository.save(item3);

        Map<Long, List<CommentDto>> result = commentService.getCommentsForItems(
                List.of(item3.getId()));

        assertThat(result).hasSize(0);
    }

    @Test
    void getCommentsForItems_ShouldOrderCommentsByCreatedDesc() {
        List<CommentDto> commentsForItem1 = commentService.getCommentsForItem(item1.getId());

        assertThat(commentsForItem1).hasSize(2);

        assertThat(commentsForItem1.get(0).text()).isEqualTo("Very useful, thanks!");
        assertThat(commentsForItem1.get(1).text()).isEqualTo("Great item!");
    }

    @Test
    void getCommentsForItem_ShouldReturnCorrectCreatedDates() {
        List<CommentDto> result = commentService.getCommentsForItem(item1.getId());

        assertThat(result).hasSize(2);

        // Проверяем что даты корректны (используя сравнение с допуском)
        assertThat(result.get(0).created())
                .isCloseTo(now.minusDays(1), within(1, ChronoUnit.MICROS));
        assertThat(result.get(1).created())
                .isCloseTo(now.minusDays(2), within(1, ChronoUnit.MICROS));
    }
}