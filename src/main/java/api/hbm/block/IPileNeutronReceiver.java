package api.hbm.block;

/** Actual deprecated X5846 Chicago-pile neutron recipient, independent from MK2/RBMK interfaces. */
public interface IPileNeutronReceiver {
    void receiveNeutrons(int neutrons);
}
