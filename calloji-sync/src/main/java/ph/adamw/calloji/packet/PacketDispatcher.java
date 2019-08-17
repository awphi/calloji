package ph.adamw.calloji.packet;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.extern.log4j.Log4j2;
import ph.adamw.calloji.util.JsonUtils;

import java.io.*;
import java.net.SocketException;
import java.util.Arrays;

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
            e.printStackTrace();
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

                /*
                int ch;
                final StringBuilder sb = new StringBuilder();
                while((ch = getInputStream().read()) != -1) {
                    sb.append((char) ch);
                    if(getInputStream().available() == 0) {
                        break;
                    }
                }
                */

                // String is split here as sometimes messages can stack up due to latency and this avoids us trying to parse multiple json
                // objects as one.
                final String[] split = content.split("\n");

                for(String i : split) {
                    if(i.equals("")) {
                        continue;
                    }

                    //TODO deal w invalid json here
                    final JsonObject json = JsonUtils.parseJson(i).getAsJsonObject();
                    final PacketType type = PacketType.getPacket(json.get("packet_id").getAsInt());

                    if(type == null) {
                        continue;
                    }

                    handleLink(type, json.get("data"));
                }
            } catch(IOException e) {
                if(e instanceof SocketException && e.getMessage().equals("Connection reset")) {
                    break;
                } else {
                    e.printStackTrace();
                }
            }
        }
    }
}
