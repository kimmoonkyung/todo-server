package org.example.service;

import org.example.model.TodoEntity;
import org.example.model.TodoRequest;
import org.example.repository.TodoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    // Mock 사용 이유
    // 외부 시스템에 의존하지 않고 자체 테스트를 실행 할 수 있어야 하기 때문.
    // 유닛 테스트는 네트워크나 DB가 연결 되지 않는다 하여 테스트가 실행이 불가하면 안 되기 때문에.
    // 또 실제 DB를 사용하게 되면 테스트 내용이 그대로 반영 되기 때문.

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    @Test
    void add() {
        // todoRepository가 세이브 메소드를 호출하여 TodoEntity 값을 받으면
        when(this.todoRepository.save(any(TodoEntity.class)))
                // 받은 엔티티 값을 반환
                .then(AdditionalAnswers.returnsFirstArg());
        TodoRequest expected = new TodoRequest();
        expected.setTitle("# TEST TITLE #");

        TodoEntity actual = this.todoService.add(expected);

        assertEquals(expected.getTitle(), actual.getTitle());
    }

    @Test
    void searchById() {
        TodoEntity entity = new TodoEntity();
        entity.setId(123L);
        entity.setTitle("# TITLE #");
        entity.setOrder(0L);
        entity.setCompleted(false);
        Optional<TodoEntity> optional = Optional.of(entity);

        given(this.todoRepository.findById(anyLong()))
                .willReturn(optional);

        TodoEntity actual = this.todoService.searchById(123L);

        TodoEntity expected = optional.get();

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getOrder(), actual.getOrder());
        assertEquals(expected.getCompleted(), actual.getCompleted());
    }

    @Test
    public void searchByIdFailed() {
        given(this.todoRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            this.todoService.searchById(123L);
        });
    }
}