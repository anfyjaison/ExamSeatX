package com.examseatx.dto;

import java.util.List;

public class AllocationRequestDto {
    private Long examSessionId;
    private List<Long> classroomIds;
    private List<Long> studentIds;
    private boolean enforceNonAdjacency = true;
    private boolean enforceDiagonal = true;
    private boolean autoAssignInvigilators = true;

    public AllocationRequestDto() {}

    public Long getExamSessionId() { return examSessionId; }
    public void setExamSessionId(Long examSessionId) { this.examSessionId = examSessionId; }

    public List<Long> getClassroomIds() { return classroomIds; }
    public void setClassroomIds(List<Long> classroomIds) { this.classroomIds = classroomIds; }

    public List<Long> getStudentIds() { return studentIds; }
    public void setStudentIds(List<Long> studentIds) { this.studentIds = studentIds; }

    public boolean isEnforceNonAdjacency() { return enforceNonAdjacency; }
    public void setEnforceNonAdjacency(boolean enforceNonAdjacency) { this.enforceNonAdjacency = enforceNonAdjacency; }

    public boolean isEnforceDiagonal() { return enforceDiagonal; }
    public void setEnforceDiagonal(boolean enforceDiagonal) { this.enforceDiagonal = enforceDiagonal; }

    public boolean isAutoAssignInvigilators() { return autoAssignInvigilators; }
    public void setAutoAssignInvigilators(boolean autoAssignInvigilators) { this.autoAssignInvigilators = autoAssignInvigilators; }
}
