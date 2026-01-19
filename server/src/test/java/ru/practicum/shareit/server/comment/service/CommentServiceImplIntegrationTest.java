package ru.practicum.shareit.server.comment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.server.AllMappersTestConfig;
import ru.practicum.shareit.server.comment.dto.CommentDto;
import ru.practicum.shareit.server.comment.model.Comment;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.entity.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({CommentServiceImpl.class, AllMappersTestConfig.class})
class CommentServiceImplIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentServiceImpl commentService;

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
        now = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);

        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        entityManager.persist(owner);

        author = new User();
        author.setName("Author");
        author.setEmail("author@example.com");
        entityManager.persist(author);

        item1 = new Item();
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setOwner(owner);
        entityManager.persist(item1);

        item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(true);
        item2.setOwner(owner);
        entityManager.persist(item2);

        comment1 = new Comment();
        comment1.setText("Great item!");
        comment1.setItem(item1);
        comment1.setAuthor(author);
        comment1.setCreated(now.minusDays(2));
        entityManager.persist(comment1);

        comment2 = new Comment();
        comment2.setText("Very useful, thanks!");
        comment2.setItem(item1);
        comment2.setAuthor(author);
        comment2.setCreated(now.minusDays(1));
        entityManager.persist(comment2);

        comment3 = new Comment();
        comment3.setText("Not bad");
        comment3.setItem(item2);
        comment3.setAuthor(author);
        comment3.setCreated(now);
        entityManager.persist(comment3);

        entityManager.flush();
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
        entityManager.persist(item3);
        entityManager.flush();

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
        assertThat(result.get(item2.getId()).getFirst().text()).isEqualTo("Not bad");
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
        entityManager.persist(item3);
        entityManager.flush();

        Map<Long, List<CommentDto>> result = commentService.getCommentsForItems(
                List.of(item3.getId()));

        assertThat(result).isEmpty();
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

        assertThat(result.get(0).created()).isEqualTo(now.minusDays(1));
        assertThat(result.get(1).created()).isEqualTo(now.minusDays(2));
    }
}