package me.bottleofglass.tebexrotation.managers;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import me.bottleofglass.tebexrotation.TebexRotation;
import me.bottleofglass.tebexrotation.data.Package;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class HttpManager {
    private TebexRotation plugin;
    private OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private Gson gson = new Gson();
    private Random random = new Random();
    private int amountOfEnabledForms;
    private int amountOfEnabledCostumes;
    private String secret;

    public HttpManager(TebexRotation plugin) {

        this.plugin = plugin;
        amountOfEnabledForms = plugin.getConfig().getNode("form_count").getInt();
        amountOfEnabledCostumes = plugin.getConfig().getNode("costume_count").getInt();
        secret = plugin.getConfig().getNode("secret").getString();

        Sponge.getScheduler().createTaskBuilder().async().execute(() ->  {
            getURL();
            Sponge.getServer().getBroadcastChannel().send(TextSerializers.formattingCode('&').deserialize("&e&cAttention players! &e&lthe store has been updated with new forms and costumes! &aType /buy &eto check out the latest options. &eGive your character a unique look and make your mark on the game. Don't miss out on this opportunity!"));
        }).delay(getTime(),TimeUnit.MILLISECONDS).interval(1, TimeUnit.DAYS);
    }


    public void getURL() {
        Request request = new Request.Builder()
                .url("https://plugin.tebex.io/packages")
                .header("X-Tebex-Secret", secret)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                plugin.getLogger().error("Get request has failed, Please report this log to the developer!");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                HashSet<Package> packages = new HashSet<>();
                //plugin.getLogger().info(response.body().string());
                JsonReader reader = gson.newJsonReader(response.body().charStream());
                reader.beginArray();
                while(reader.hasNext()) {
                    packages.add(readPackage(reader));
                }
                reader.endArray();
                reader.close();
                //TODO: add check for forms category
                //TODO: disable enabled packages
                //TODO: select 5/10 random package from disabled packages to enable.
                Set<Package> packageSet = packages.stream().filter(x -> x.getCategoryId().equals("Forms")).collect(Collectors.toSet());
                for(int i = 0; i < amountOfEnabledForms; i++) {
                    Package pack = (Package) packageSet.toArray()[random.nextInt(packageSet.size())];
                    if(pack.isDisabled())
                        updatePackage(pack, false);
                    packageSet.remove(pack);
                }
                for(Package pack : packageSet) {
                    if(!pack.isDisabled())
                        updatePackage(pack, true);
                }
                packageSet.clear();

                packageSet = packages.stream().filter(x -> x.getCategoryId().equals("Costumes")).collect(Collectors.toSet());
                for(int i = 0; i < amountOfEnabledCostumes; i++) {
                    Package pack = (Package) packageSet.toArray()[random.nextInt(packageSet.size())];
                    if(pack.isDisabled())
                        updatePackage(pack, false);
                    packageSet.remove(pack);
                }
                for(Package pack : packageSet) {
                    if(!pack.isDisabled())
                        updatePackage(pack, true);
                }
                packages.clear();
                response.close();
            }
        });
    }
    private void updatePackage(Package pack, boolean disabled) {
        RequestBody requestBody = RequestBody.create("{\"disabled\":"+ Boolean.valueOf(disabled).toString() + "}", JSON);
        Request request = new Request.Builder()
                .url("https://plugin.tebex.io/package/" + pack.getId())
                .header("X-Tebex-Secret", secret)
                .put(requestBody)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                plugin.getLogger().error("Get request has failed, Please report this log to the developer!");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    plugin.getLogger().info(("Code: " + response.code()) + (disabled ? " Disabled  pack id : " + pack.getId() : "Enabled  pack id : " + pack.getId() ));
                } else {
                    plugin.getLogger().error("Error " + (disabled ? "disabling" : "enabling") + " a form, ID : " + pack.getId());
                    plugin.getLogger().error("Error code : " + response.code());
                    if(response.body() != null)
                        plugin.getLogger().error(response.body().string());
                }
                response.close();
            }
        });
    }
    private Package readPackage(JsonReader reader) throws IOException {
        reader.beginObject();
        String id = null;
        boolean disabled = true;
        String categoryId = null;
        outerloop:
        while(reader.hasNext()) {
            String name = reader.nextName();
            switch (name.toLowerCase()) {
                case "id":
                    id = reader.nextString();
                    break;
                case "disabled":
                    disabled = reader.nextBoolean();
                    break;
                case "category":
                    reader.beginObject();
                    while (reader.hasNext()) {
                        if(reader.nextName().equals("name")) {
                            categoryId = reader.nextString();
                            reader.endObject();
                            continue outerloop;
                        }
                        reader.skipValue();
                    }
                    reader.endObject();
                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
        return new Package(id,disabled,categoryId);
    }

    public long getTime() {
        return (System.currentTimeMillis() % TimeUnit.DAYS.toMillis(1)) < TimeUnit.MINUTES.toMillis(810) ? TimeUnit.MINUTES.toMillis(810) - (System.currentTimeMillis() % TimeUnit.DAYS.toMillis(1)) : TimeUnit.DAYS.toMillis(1) - ((System.currentTimeMillis() % TimeUnit.DAYS.toMillis(1)) - TimeUnit.MINUTES.toMillis(810)) ;
    }
}
