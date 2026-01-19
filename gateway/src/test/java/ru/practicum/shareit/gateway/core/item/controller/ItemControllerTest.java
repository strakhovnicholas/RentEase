package ru.practicum.shareit.gateway.core.item.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.gateway.core.item.ItemClient;
import ru.practicum.shareit.gateway.core.item.dto.comment.CommentRequestDto;
import ru.practicum.shareit.gateway.core.item.dto.item.CreateItemDto;
import ru.practicum.shareit.gateway.core.item.dto.item.ItemUpdateDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    private ItemController itemController;

    @Mock
    private ItemClient itemClient;

    private Pageable pageable;

    private CreateItemDto createItemDto;
    private ItemUpdateDto updateDto;
    private CommentRequestDto commentRequestDto;

    @BeforeEach
    void setUp() {
        itemController = new ItemController(itemClient);

        createItemDto = CreateItemDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        updateDto = ItemUpdateDto.builder()
                .name("Updated Name")
                .description("Updated Description")
                .available(false)
                .build();

        commentRequestDto = CommentRequestDto.builder()
                .text("Great item!")
                .build();
    }

    @Test
    void getItemById_ShouldCallClientWithCorrectParameters() {
        Long itemId = 1L;
        Long ownerId = 2L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("item details");
        when(itemClient.getItemById(itemId, ownerId)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemController.getItemById(itemId, ownerId);

        verify(itemClient).getItemById(itemId, ownerId);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void updateItemById_ShouldCallClientWithCorrectParameters() {
        Long itemId = 1L;
        Long userId = 2L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("updated item");
        when(itemClient.updateItem(itemId, updateDto, userId)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                itemController.updateItemById(updateDto, itemId, userId);

        verify(itemClient).updateItem(itemId, updateDto, userId);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void createItem_ShouldCallClientWithCorrectParameters() {
        Long ownerId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.status(HttpStatus.CREATED).body("created item");
        when(itemClient.createItem(ownerId, createItemDto)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                itemController.createItem(ownerId, createItemDto);

        verify(itemClient).createItem(ownerId, createItemDto);
        assertEquals(expectedResponse, actualResponse);
        assertEquals(HttpStatus.CREATED, actualResponse.getStatusCode());
    }

    @Test
    void createItemComment_ShouldCallClientWithCorrectParameters() {
        Long itemId = 1L;
        Long userId = 2L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("comment created");
        when(itemClient.createComment(itemId, userId, commentRequestDto)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                itemController.createItemComment(commentRequestDto, userId, itemId);

        verify(itemClient).createComment(itemId, userId, commentRequestDto);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void searchItemsByQuery_WithEmptyText_ShouldCallClientWithEmptyString() {
        String text = "";
        Long ownerId = 1L;
        int page = 0;
        int size = 20;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("search results");
        when(itemClient.searchItems(text, ownerId, page * size, size)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                itemController.searchItemsByQuery(text, ownerId, pageable);

        verify(itemClient).searchItems(text, ownerId, page * size, size);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void searchItemsByQuery_WithText_ShouldCallClientWithCorrectParameters() {
        String text = "drill";
        Long ownerId = 1L;
        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("search results");
        when(itemClient.searchItems(text, ownerId, page * size, size)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                itemController.searchItemsByQuery(text, ownerId, pageable);

        verify(itemClient).searchItems(text, ownerId, page * size, size);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void searchItemsByQuery_WithDefaultPageable_ShouldUseDefaultValues() {
        String text = "test";
        Long ownerId = 1L;
        int page = 0;
        int size = 20;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("search results");
        when(itemClient.searchItems(text, ownerId, page * size, size)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                itemController.searchItemsByQuery(text, ownerId, pageable);

        verify(itemClient).searchItems(text, ownerId, 0, 20);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getUserItems_ShouldCallClientWithCorrectParameters() {
        Long ownerId = 1L;
        int page = 0;
        int size = 20;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("user items");
        when(itemClient.getUserItems(ownerId, page * size, size)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                itemController.getUserItems(ownerId, pageable);

        verify(itemClient).getUserItems(ownerId, page * size, size);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getUserItems_WithCustomPageable_ShouldCalculateFromParameterCorrectly() {
        Long ownerId = 1L;
        int page = 2;
        int size = 5;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        int expectedFrom = page * size;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("user items");
        when(itemClient.getUserItems(ownerId, expectedFrom, size)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                itemController.getUserItems(ownerId, pageable);

        verify(itemClient).getUserItems(ownerId, expectedFrom, size);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void createItem_WithArgumentCaptor_ShouldPassCorrectDto() {
        Long ownerId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(itemClient.createItem(eq(ownerId), any(CreateItemDto.class))).thenReturn(expectedResponse);

        itemController.createItem(ownerId, createItemDto);

        ArgumentCaptor<CreateItemDto> dtoCaptor = ArgumentCaptor.forClass(CreateItemDto.class);
        verify(itemClient).createItem(eq(ownerId), dtoCaptor.capture());

        CreateItemDto capturedDto = dtoCaptor.getValue();
        assertEquals(createItemDto.name(), capturedDto.name());
        assertEquals(createItemDto.description(), capturedDto.description());
        assertEquals(createItemDto.available(), capturedDto.available());
    }

    @Test
    void updateItemById_WithPartialUpdate_ShouldPassCorrectDto() {
        Long itemId = 1L;
        Long userId = 2L;

        ItemUpdateDto partialUpdate = ItemUpdateDto.builder()
                .name("Only Name Updated")
                .build();

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("updated");
        when(itemClient.updateItem(itemId, partialUpdate, userId)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                itemController.updateItemById(partialUpdate, itemId, userId);

        verify(itemClient).updateItem(itemId, partialUpdate, userId);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void controllerMethods_ShouldHandleErrorResponses() {
        Long itemId = 999L;
        Long userId = 1L;
        ResponseEntity<Object> errorResponse =
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");

        when(itemClient.getItemById(itemId, userId)).thenReturn(errorResponse);

        ResponseEntity<Object> actualResponse =
                itemController.getItemById(itemId, userId);

        assertEquals(HttpStatus.NOT_FOUND, actualResponse.getStatusCode());
        assertEquals("Item not found", actualResponse.getBody());
    }

    @Test
    void createItemComment_ShouldPassCorrectCommentText() {
        Long itemId = 1L;
        Long userId = 2L;
        String commentText = "Excellent condition, would recommend!";

        CommentRequestDto comment = CommentRequestDto.builder()
                .text(commentText)
                .build();

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("comment added");
        when(itemClient.createComment(itemId, userId, comment)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse =
                itemController.createItemComment(comment, userId, itemId);

        ArgumentCaptor<CommentRequestDto> commentCaptor =
                ArgumentCaptor.forClass(CommentRequestDto.class);
        verify(itemClient).createComment(eq(itemId), eq(userId), commentCaptor.capture());

        assertEquals(commentText, commentCaptor.getValue().text());
        assertEquals(expectedResponse, actualResponse);
    }
}