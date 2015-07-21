package com.gobliip.chronos.server.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

/**
 * Created by lsamayoa on 19/07/15.
 */
@Entity(name = "work_periods")
public class WorkPeriod extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Can be of type START, RESUME, HEARTBEAT
     */
    @OneToOne(optional = false)
    @JoinColumn(name = "start_moment_id")
    private Moment start;

    /**
     * Can be of type STOP, PAUSE, HEARTBEAT
     */
    @OneToOne(optional = false)
    @JoinColumn(name = "end_moment_id")
    private Moment end;

    @Column(name = "mouse_actions_count")
    private int mouseActionsCount;

    @Column(name = "keyboard_actions_count")
    private int keyboardActionsCount;

    @ManyToOne
    @JoinColumn(name = "work_session_id")
    @JsonIgnore
    private WorkSession workSession;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private WorkPeriod parent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Moment getStart() {
        return start;
    }

    public void setStart(Moment start) {
        this.start = start;
    }

    public Moment getEnd() {
        return end;
    }

    public void setEnd(Moment end) {
        this.end = end;
    }

    public int getMouseActionsCount() {
        return mouseActionsCount;
    }

    public void setMouseActionsCount(int mouseActionsCount) {
        this.mouseActionsCount = mouseActionsCount;
    }

    public int getKeyboardActionsCount() {
        return keyboardActionsCount;
    }

    public void setKeyboardActionsCount(int keyboardActionsCount) {
        this.keyboardActionsCount = keyboardActionsCount;
    }

    public WorkSession getWorkSession() {
        return workSession;
    }

    public void setWorkSession(WorkSession workSession) {
        this.workSession = workSession;
    }

    public WorkPeriod getParent() {
        return parent;
    }

    public void setParent(WorkPeriod parent) {
        this.parent = parent;
    }
}
