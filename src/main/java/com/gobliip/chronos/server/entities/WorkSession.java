package com.gobliip.chronos.server.entities;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lsamayoa on 20/07/15.
 */
@Entity(name = "work_sessions")
public class WorkSession extends BaseEntity {

    public enum WorkSessionStatus {
        OPEN, PAUSED, CLOSED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JoinColumn(name = "tracking_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("trackingId")
    private Tracking tracking;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private WorkSessionStatus status;

    @Column(name = "owner")
    private String owner;

    @Column(name = "keyboard_actions_count")
    private int keyboardActionsCount;

    @Column(name = "mouse_actions_count")
    private int mouseActionsCount;

    @OneToMany(mappedBy = "workSession", cascade = {CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH}, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<WorkPeriod> loggedPeriods = new LinkedList<>();

    @OneToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH})
    @JoinColumn(name = "last_logged_period_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("lastLoggedPeriodId")
    private WorkPeriod lastLoggedPeriod;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tracking getTracking() {
        return tracking;
    }

    public void setTracking(Tracking tracking) {
        this.tracking = tracking;
    }

    public WorkSessionStatus getStatus() {
        return status;
    }

    public void setStatus(WorkSessionStatus status) {
        this.status = status;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getKeyboardActionsCount() {
        return keyboardActionsCount;
    }

    public void setKeyboardActionsCount(int keyboardActionsCount) {
        this.keyboardActionsCount = keyboardActionsCount;
    }

    public int getMouseActionsCount() {
        return mouseActionsCount;
    }

    public void setMouseActionsCount(int mouseActionsCount) {
        this.mouseActionsCount = mouseActionsCount;
    }

    public List<WorkPeriod> getLoggedPeriods() {
        return loggedPeriods;
    }

    public void setLoggedPeriods(List<WorkPeriod> loggedPeriods) {
        this.loggedPeriods = loggedPeriods;
    }

    public WorkPeriod getLastLoggedPeriod() {
        return lastLoggedPeriod;
    }

    public void setLastLoggedPeriod(WorkPeriod lastLoggedPeriod) {
        this.lastLoggedPeriod = lastLoggedPeriod;
    }
}
