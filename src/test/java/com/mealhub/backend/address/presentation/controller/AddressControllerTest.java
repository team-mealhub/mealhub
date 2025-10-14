package com.mealhub.backend.address.presentation.controller;

import com.mealhub.backend.address.application.service.AddressService;
import com.mealhub.backend.address.presentation.dto.request.AddressRequest;
import com.mealhub.backend.address.presentation.dto.response.AddressResponse;
import com.mealhub.backend.global.domain.application.libs.MessageUtils;
import com.mealhub.backend.global.infrastructure.config.security.UserDetailsImpl;
import com.mealhub.backend.user.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AddressController.class)
@MockitoBean(types = AddressService.class)
@MockitoBean(types = MessageUtils.class)
public class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AddressService addressService;

    @WithMockUser
    @Test
    @DisplayName("주소 등록 성공")
    void createAddress() throws Exception {
        UUID id = UUID.randomUUID();
        AddressResponse addressResponse = new AddressResponse(
                id, "우리집", true, "서울시 강남구", null, 127.0, 37.0, "메모");

        Mockito.when(addressService.create(any(), any(AddressRequest.class))).thenReturn(addressResponse);

        UserDetailsImpl mockUserDetails = mock(UserDetailsImpl.class);
        Mockito.when(mockUserDetails.toUser()).thenReturn(new User());

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/addresses")
                        .with(csrf())
                        .with(authentication(new UsernamePasswordAuthenticationToken(mockUserDetails, null, mockUserDetails.getAuthorities())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
{
"name": "우리집",
"isDefault": true,
"address": "서울시 강남구",
"longitude": 127.0,
"latitude": 37.0,
"memo": "메모"
}
"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("우리집"))
                .andExpect(jsonPath("$.isDefault").value(true))
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("주소 단일 조회")
    @WithMockUser
    void getAddress() throws Exception {
        UUID id = UUID.randomUUID();
        AddressResponse addressResponse = new AddressResponse(
                id, "우리집", true, "서울시 강남구", null, 127.0, 37.0, "메모");

        Mockito.when(addressService.getAddress(any(), eq(id))).thenReturn(addressResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/addresses/" + id)
                        .with(csrf())
                        .with(authentication(new UsernamePasswordAuthenticationToken(mock(UserDetailsImpl.class), null, List.of())))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("우리집"))
                .andExpect(jsonPath("$.isDefault").value(true))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("주소 전체 조회 - 페이징")
    @WithMockUser
    void getAllAddresses() throws Exception {
        UUID id = UUID.randomUUID();
        AddressResponse addressResponse = new AddressResponse(
                id, "우리집", true, "서울시 강남구", null, 127.0, 37.0, "메모");

        Page<AddressResponse> mockPage = new PageImpl<>(List.of(addressResponse));

        Mockito.when(addressService.getAllAddresses(any(), any(), any())).thenReturn(mockPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/addresses")
                        .with(csrf())
                        .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, List.of()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("우리집"))
                .andDo(MockMvcResultHandlers.print());
    }

    private UserDetailsImpl mockPrincipal() {
        UserDetailsImpl mockUserDetails = Mockito.mock(UserDetailsImpl.class);
        Mockito.when(mockUserDetails.toUser()).thenReturn(new User());
        return mockUserDetails;
    }

    @Test
    @DisplayName("주소 수정 성공")
    @WithMockUser
    void updateAddress() throws Exception {
        UUID id = UUID.randomUUID();
        AddressResponse addressResponse = new AddressResponse(
                id, "회사", false, "서울시 서초구", null, 127.1, 37.1, "출근용"
        );

        Mockito.when(addressService.updateAddress(any(), eq(id), any(AddressRequest.class))).thenReturn(addressResponse);

        mockMvc.perform(MockMvcRequestBuilders.put("/v1/addresses/" + id)
                        .with(csrf())
                        .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, List.of())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
{
"name": "회사",
"isDefault": false,
"address": "서울시 서초구",
"longitude": 127.1,
"latitude": 37.1,
"memo": "출근용"

}
"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("회사"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("주소 soft-delete 성공")
    @WithMockUser
    void deleteAddress() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/addresses/" + id)
                        .with(csrf())
                        .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, List.of()))))
                .andExpect(status().isNoContent())
                .andDo(MockMvcResultHandlers.print());
        Mockito.verify(addressService, Mockito.times(1)).deleteAddress(any(), eq(id));
    }

    @Test
    @DisplayName("주소 검색 성공")
    @WithMockUser
    void searchAddresses() throws Exception {
        UUID id = UUID.randomUUID();
        AddressResponse addressResponse = new AddressResponse(
                id, "우리집", true, "서울시 강남구", null, 127.0, 37.0, "메모");

        Page<AddressResponse> mockPage = new PageImpl<>(List.of(addressResponse));

        Mockito.when(addressService.getAllAddresses(any(), eq("우리"), any())).thenReturn(mockPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/addresses")
                        .param("keyword", "우리")
                        .with(csrf())
                        .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, List.of()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("우리집"))
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    @DisplayName("기본 주소 변경 성공")
    @WithMockUser
    void changeDefaultAddress() throws Exception {
        UUID id = UUID.randomUUID();
        AddressResponse addressResponse = new AddressResponse(
                id, "우리집", true, "서울시 강남구", null, 127.0, 37.0, "메모");

        Mockito.when(addressService.changeDefault(any(), eq(id))).thenReturn(addressResponse);

        mockMvc.perform(MockMvcRequestBuilders.patch("/v1/addresses/" + id + "/default")
                        .with(csrf())
                        .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, List.of()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isDefault").value(true))
                .andDo(MockMvcResultHandlers.print());

    }
}

