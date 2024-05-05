package fhict.boards.service;

import fhict.boards.domain.dto.IssueCreateRequest;
import fhict.boards.domain.dto.IssueResponse;
import fhict.boards.domain.dto.IssueUpdateRequest;
import fhict.boards.domain.enums.IssueStatus;
import fhict.boards.repository.entity.Issue;

import java.util.List;

public interface IssueService {
    IssueResponse createIssue(Long projectId, IssueCreateRequest request);
    List<IssueResponse> getIssuesByProjectId(Long id);
    IssueResponse getIssueById(Long issueId, Long projectId);
    IssueResponse updateIssue(Long issueId, Long projectId, IssueUpdateRequest request);
    IssueResponse updateStatus(Long issueId, Long projectId, IssueStatus status);
    void deleteIssue(Long issueId, Long projectId);
    Issue getIssueByIdOrThrowNotFound(Long id);
}
