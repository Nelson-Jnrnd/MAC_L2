package ch.heig.mac;

import org.neo4j.driver.*;

public class Main {

    // TODO: Configure credentials to allow connection to your Neo4j instance
    public static Driver openConnection() {
        var uri = "neo4j://localhost:7687";
        var username = "neo4j";
        var password = "neo4j";
        var config = Config.defaultConfig();

        return GraphDatabase.driver(uri, AuthTokens.basic(username, password), config);
    }

    public static void main(String[] args) {
        try (var driver = openConnection()) {
            var requests = new Requests(driver);
            requests.getDbLabels().forEach(System.out::println);
        }
    }
}
