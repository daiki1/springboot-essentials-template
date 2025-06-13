package com.project.spring_project.service.impl.user;
import com.project.spring_project.dto.RoleDTO;
import com.project.spring_project.mapper.RoleMapper;
import com.project.spring_project.repository.user.RoleRepository;
import com.project.spring_project.service.user.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public List<RoleDTO> getAllRoles() {
        return roleMapper.toDTOs(roleRepository.findAll());
    }
}