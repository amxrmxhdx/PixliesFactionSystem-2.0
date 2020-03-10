package me.mickmmars.factions.war.data;

import me.mickmmars.factions.war.CBReason;

import java.util.List;

public class CasusBelli {

    private String id;
    private String AttackerId;
    private String DefenderId;
    private List<String> evidence;
    private Boolean accepted;
    private String reason;
    private Boolean rejected;
    private Boolean pending;
    private Boolean used;

    public CasusBelli(String id, String AttackerId, String DefenderId, List<String> evidence, Boolean accepted, String reason, Boolean rejected, Boolean pending, Boolean used) {
        this.id = id;
        this.AttackerId = AttackerId;
        this.DefenderId = DefenderId;
        this.evidence = evidence;
        this.accepted = accepted;
        this.reason = reason;
        this.rejected = rejected;
        this.pending = pending;
        this.used = used;
    }

    public Boolean isUsed() { return used; }
    public void setUsed(Boolean used) { this.used = used; }

    public Boolean isPending() { return pending; }
    public void setPending(Boolean pending) { this.pending = pending; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAttackerId() { return AttackerId;}
    public void setAttackerId(String AttackerId) { this.AttackerId = AttackerId; }

    public String getDefenderId() { return DefenderId; }
    public void setDefenderId(String DefenderId) { this.DefenderId = DefenderId; }

    public List<String> getEvidence() { return evidence; }
    public void setEvidence(List<String> evidence) { this.evidence = evidence; }

    public Boolean getAccepted() { return accepted; }
    public void setAccepted(Boolean accepted) { this.accepted = accepted; }

    public String getReason() { return reason; }
    public CBReason getReasonAsEnum() { return CBReason.getReasonByName(reason); }
    public void setReason(String reason) { this.reason = reason; }

    public Boolean isRejected() { return rejected; }
    public void setRejected(Boolean rejected) { this.rejected = rejected; }

}
