package me.mickmmars.factions.war.data;

import me.mickmmars.factions.Factions;
import me.mickmmars.factions.factions.data.FactionData;
import me.mickmmars.factions.war.CBReason;

import java.util.ArrayList;
import java.util.List;

public class CasusBelli {

    private static Factions instance = Factions.getInstance();

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

    // STATICS
    public static List<CasusBelli> listCBs() {
        List<CasusBelli> Cbs = new ArrayList<>();
        for (FactionData facs : instance.getFactionManager().getFactions()) {
            if (facs.listCbs().size() != 0)
                for (CasusBelli cb : facs.listCbs())
                    Cbs.add(cb);
        }
        return Cbs;
    }

    public static CasusBelli createCB(FactionData attacker, FactionData defender, String evidence, CBReason reason) {
        List<String> evidences = new ArrayList<>();
        evidences.add(evidence);
        CasusBelli cb = new CasusBelli(instance.generateKey(7), attacker.getId(), defender.getId(), evidences, false, reason.getName(), false, true, false);
        List<CasusBelli> Cbs = new ArrayList<>(attacker.listCbs());
        Cbs.add(cb);
        attacker.setCbs(Cbs);
        attacker.updateData();
        return cb;
    }

    public static Boolean exists(CasusBelli cb) {
        return listCBs().contains(cb);
    }

    public static void deleteCb(CasusBelli cb) {
        if (instance.getFactionManager().getFactionById(cb.getAttackerId()).listCbs().contains(cb)) {
            List<CasusBelli> Cbs = new ArrayList<>(instance.getFactionManager().getFactionById(cb.getAttackerId()).listCbs());
            Cbs.remove(cb);
            instance.getFactionManager().getFactionById(cb.getAttackerId()).setCbs(Cbs);
            instance.getFactionManager().updateFactionData(instance.getFactionManager().getFactionById(cb.getAttackerId()));
        }
    }

    public static Boolean checkIfFactionAlreadySentCB(FactionData fac, FactionData def) {
        if (fac.listCbs().size() == 0) return false;
        for (CasusBelli cbs : fac.listCbs())
            if (cbs.isPending())
                return true;
        return false;
    }

    public static CasusBelli getCbById(String id) {
        for (CasusBelli cb : listCBs())
            if (cb.getId().equals(id))
                return cb;
        return null;
    }

    public static CasusBelli getUsed(FactionData attacker) {
        for (CasusBelli cb : attacker.listCbs())
            if (cb.isUsed())
                return cb;
        return null;
    }

    public static void acceptCb(CasusBelli cb) {
        FactionData attacker = instance.getFactionManager().getFactionById(cb.getAttackerId());
        List<CasusBelli> cbs = new ArrayList<>(attacker.listCbs());
        cbs.remove(cb);
        cb.setAccepted(true);
        cb.setPending(false);
        cbs.add(cb);
        instance.getFactionManager().updateFactionData(attacker);
    }

    public static void rejectCb(CasusBelli cb) {
        FactionData attacker = instance.getFactionManager().getFactionById(cb.getAttackerId());
        List<CasusBelli> cbs = new ArrayList<>(attacker.listCbs());
        cbs.remove(cb);
        cb.setRejected(true);
        cb.setPending(false);
        cbs.add(cb);
        instance.getFactionManager().updateFactionData(attacker);
    }

    public static void useCB(CasusBelli cb) {
        List<CasusBelli> cbs = new ArrayList<>(instance.getFactionManager().getFactionById(cb.getAttackerId()).listCbs());
        cbs.remove(cb);
        cb.setUsed(true);
        cbs.add(cb);
        instance.getFactionManager().getFactionById(cb.getAttackerId()).setCbs(cbs);
        instance.getFactionManager().updateFactionData(instance.getFactionManager().getFactionById(cb.getAttackerId()));
    }

}
