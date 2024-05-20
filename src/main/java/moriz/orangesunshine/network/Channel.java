package moriz.orangesunshine.network;

import com.sollace.fabwork.api.packets.S2CPacketType;
import com.sollace.fabwork.api.packets.SimpleNetworking;

import moriz.orangesunshine.OrangeSunshine;

/**
 * @author Sollace
 * @since 1 Jan 2023
 */
public interface Channel {
    S2CPacketType<MsgDrugProperties> UPDATE_DRUG_PROPERTIES = SimpleNetworking.serverToClient(OrangeSunshine.id("update_drug_properties"), MsgDrugProperties::new);
    S2CPacketType<MsgHallucinate> HALLUCINATE = SimpleNetworking.serverToClient(OrangeSunshine.id("hallucinate"), MsgHallucinate::new);

    static void bootstrap() { }

}
