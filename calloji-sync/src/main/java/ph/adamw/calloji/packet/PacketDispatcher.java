package ph.adamw.calloji.packet;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.extern.log4j.Log4j2;
import ph.adamw.calloji.util.JsonUtils;

import java.io.*;

@Log4j2
public abstract class PacketDispatcher {
    protected abstract OutputStream getOutputStream();
    protected abstract InputStream getInputStream();
    protected abstract void handleLink(PacketType packetType, JsonElement content);

    protected abstract boolean isConnected();

    protected void startReceiving() {
        new Thread(this::receive, "PktIn").start();
    }

    public boolean send(PacketType type, Object content) {
        if(!isConnected()) {
            return false;
        }

        final JsonObject parent = new JsonObject();

        parent.addProperty("packet_id", type.getId());

        log.debug("Dispatching " + type.name() + ": " + content.toString());

        if(content instanceof JsonElement) {
            parent.add("data", (JsonElement) content);
        } else {
            parent.add("data", JsonUtils.getJsonElement(content));
        }

        try {
            getOutputStream().write((parent.toString() + "\n").getBytes());
        } catch (IOException e) {
            log.trace(e);
            return false;
        }

        return true;
    }

    private void receive() {
        final BufferedReader br = new BufferedReader(new InputStreamReader(getInputStream()));

        while(isConnected()) {
            try {
                if(!br.ready()) {
                    continue;
                }

                String content = br.readLine();

                // String is split here as sometimes messages can stack up due to latency and this avoids us trying to parse multiple json
                // objects as one.
                final String[] split = content.split("\n");

                for(String i : split) {
                    if(i.equals("")) {
                        continue;
                    }

                    final JsonElement json;

                    try {
                        json = JsonUtils.parseJson(i);
                    } catch (JsonSyntaxException e) {
                        log.trace(e);
                        continue;
                    }

                    if(!json.isJsonObject()) {
                        continue;
                    }

                    final JsonObject obj = json.getAsJsonObject();

                    final JsonElement id = obj.get("packet_id");

                    if(!id.isJsonPrimitive()) {
                        continue;
                    }

                    final int idAsInt;

                    try {
                        idAsInt = id.getAsInt();
                    } catch (NumberFormatException e) {
                        log.trace(e);
                        continue;
                    }

                    final PacketType type = PacketType.getPacket(idAsInt);

                    if(type == null) {
                        continue;
                    }

                    handleLink(type, obj.get("data"));
                }

            } catch(IOException e) {
                log.trace(e);
            }
        }
    }
}
