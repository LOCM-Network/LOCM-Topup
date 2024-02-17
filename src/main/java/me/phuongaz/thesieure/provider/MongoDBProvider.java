package me.phuongaz.thesieure.provider;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.phuongaz.thesieure.Loader;
import org.bson.Document;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class MongoDBProvider extends Provider {

    private MongoClient dbClient;
    private MongoCollection<Document> cardsCollection;
    private MongoCollection<Document> pendingCollection;

    private MongoCollection<Document> banksCollection;

    @Override
    public void connect() {
        FileConfiguration config = Loader.getInstance().getConfig();
        String uri = config.getString("provider.host");
        String port = config.getString("provider.port");

        try(MongoClient mongoClient = MongoClients.create("mongodb://" + uri + ":" + port + "/?retryWrites=true&w=majority")
        ) {
            this.dbClient = mongoClient;
            MongoDatabase database = dbClient.getDatabase("donate");

            cardsCollection = database.getCollection("cards");
            pendingCollection = database.getCollection("pending");
            banksCollection = database.getCollection("banks");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertCard(String player, String serial, String pin, int amount) {
        Document document = new Document("player", player)
                .append("serial", serial)
                .append("pin", pin)
                .append("amount", amount);
        cardsCollection.insertOne(document);
    }

    @Override
    public void insertPending(String player, String serial, String pin, int amount) {
        Document document = new Document("player", player)
                .append("serial", serial)
                .append("pin", pin)
                .append("amount", amount);
        pendingCollection.insertOne(document);
    }

    @Override
    public void insertBank(String player, int amount) {
        Document document = new Document("player", player)
                .append("amount", amount);
        banksCollection.insertOne(document);
    }

    @Override
    public List<String> getPending(String player) {
        Document document = new Document("player", player);
        return pendingCollection.find(document).map(doc -> doc.getString("serial") + ":" + doc.getString("pin")).into(new java.util.ArrayList<>());
    }

    @Override
    public void removePending(String serial, String pin) {
        Document document = new Document("serial", serial)
                .append("pin", pin);
        pendingCollection.deleteOne(document);
    }

    @Override
    public void close() {
        dbClient.close();
    }

}
