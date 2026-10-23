package com.hbm.machine.reactor.rbmk;
/** Actual source IRBMKFluxReceiver.NType order; ANY is fuel designation only. */
public enum NeutronType {FAST("trait.rbmk.neutron.fast"),SLOW("trait.rbmk.neutron.slow"),ANY("trait.rbmk.neutron.any");public final String unlocalized;NeutronType(String key){unlocalized=key;}}
