package fhict.boards.service;

import fhict.boards.domain.dto.MemberResponse;
import fhict.boards.domain.dto.MemberRoleUpdateRequest;
import fhict.boards.domain.enums.MemberRole;
import fhict.boards.repository.entity.Member;

import java.util.EnumSet;
import java.util.List;

public interface MemberService {
    MemberResponse getMemberById(Long id);
    List<MemberResponse> getMembersByProjectId(Long id);
    void removeMember(Long id);
    MemberResponse updateMemberRole(Long id, MemberRoleUpdateRequest request);
    MemberResponse getCurrentMember(Long userId, Long projectId);
    Member assertMemberIsAuthorized(Long projectId, EnumSet<MemberRole> rolesAllowed);
}
