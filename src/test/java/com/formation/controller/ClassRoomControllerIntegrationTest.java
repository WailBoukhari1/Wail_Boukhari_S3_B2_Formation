package com.formation.controller;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formation.entity.ClassRoom;
import com.formation.service.ClassRoomService;

@WebMvcTest(ClassRoomController.class)
class ClassRoomControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClassRoomService classRoomService;

    @Autowired
    private ObjectMapper objectMapper;

    private ClassRoom testClassRoom;

    @BeforeEach
    void setUp() {
        testClassRoom = new ClassRoom();
        testClassRoom.setId(1L);
        testClassRoom.setName("Test Room");
        testClassRoom.setRoomNumber("TR-101");
        testClassRoom.setMaxCapacity(30);
        testClassRoom.setCurrentCapacity(0);
    }

    @Test
    void whenCreateClassRoom_thenReturn201() throws Exception {
        when(classRoomService.save(any(ClassRoom.class))).thenReturn(testClassRoom);

        mockMvc.perform(post("/api/classrooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testClassRoom)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomNumber").value(testClassRoom.getRoomNumber()));
    }

    @Test
    void whenGetClassRoomById_thenReturnClassRoom() throws Exception {
        when(classRoomService.findById(1L)).thenReturn(testClassRoom);

        mockMvc.perform(get("/api/classrooms/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.roomNumber").value(testClassRoom.getRoomNumber()));
    }

    @Test
    void whenGetAllClassRooms_thenReturnClassRoomPage() throws Exception {
        List<ClassRoom> classRooms = new ArrayList<>();
        classRooms.add(testClassRoom);
        Page<ClassRoom> classRoomPage = new PageImpl<>(classRooms);

        when(classRoomService.findAll(any(Pageable.class))).thenReturn(classRoomPage);

        mockMvc.perform(get("/api/classrooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].roomNumber").value(testClassRoom.getRoomNumber()));
    }

    @Test
    void whenUpdateClassRoom_thenReturnUpdatedClassRoom() throws Exception {
        when(classRoomService.update(any(ClassRoom.class))).thenReturn(testClassRoom);

        mockMvc.perform(put("/api/classrooms/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testClassRoom)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomNumber").value(testClassRoom.getRoomNumber()));
    }

    @Test
    void whenDeleteClassRoom_thenReturn204() throws Exception {
        mockMvc.perform(delete("/api/classrooms/1"))
                .andExpect(status().isNoContent());
    }
}
