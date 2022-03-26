package ch.heig.mac;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.Neo4jException;

public class Requests {
    private static final Logger LOGGER = Logger.getLogger(Requests.class.getName());
    private final Driver driver;

    private List<Record> runStandardQuery(String query) {
        try (Session session = driver.session()) {
            Result result = session.run(query);
            return result.list();
        }
    }

    public Requests(Driver driver) {
        this.driver = driver;
    }

    public List<String> getDbLabels() {
        var dbVisualizationQuery = "CALL db.labels";

        try (var session = driver.session()) {
            var result = session.run(dbVisualizationQuery);
            return result.list(t -> t.get("label").asString());
        }
    }

    public List<Record> possibleSpreaders() {
        String query = "MATCH (p1:Person {healthstatus:\"Sick\"})-[visit1:VISITS]->(:Place)<-[visit2:VISITS]-(p2:Person {healthstatus:\"Healthy\"})\n" +
                "WHERE visit1.starttime > p1.confirmedtime AND visit2.starttime > p1.confirmedtime\n" +
                "RETURN DISTINCT p1.name as sickName";

        return runStandardQuery(query);
    }

    public List<Record> possibleSpreadCounts() {
        String query = "MATCH (p1:Person {healthstatus:\"Sick\"})-[visit1:VISITS]->(:Place)<-[visit2:VISITS]-(p2:Person {healthstatus:\"Healthy\"})\n" +
        "WHERE visit1.starttime > p1.confirmedtime AND visit2.starttime > p1.confirmedtime\n" +
        "RETURN p1.name as sickName, COUNT(p2) AS nbHealthy";

        return runStandardQuery(query);
    }

    public List<Record> carelessPeople() {
        String query = "MATCH (person:Person {healthstatus:\"Sick\"})-[:VISITS]->(place:Place)\n" +
                "WITH person, count(place.name) AS nbPlaces\n" +
                "WHERE nbPlaces > 10\n" +
                "RETURN person.name AS sickName, nbPlaces ORDER BY nbPlaces DESC";

        return runStandardQuery(query);
    }

    public List<Record> sociallyCareful() {
        String query = "MATCH (person:Person {healthstatus:\"Sick\"})-[visit:VISITS]-(place:Place {type:\"Bar\"})\n" +
                "WHERE all(_ in [person.confirmedtime, visit.starttime] WHERE visit.starttime < person.confirmedtime)\n" +
                "RETURN DISTINCT(person.name) as sickName";

        return runStandardQuery(query);
    }

    public List<Record> peopleToInform() {
        throw new UnsupportedOperationException("Not implemented, yet");
    }

    public List<Record> setHighRisk() {
        throw new UnsupportedOperationException("Not implemented, yet");
    }

    public List<Record> healthyCompanionsOf(String name) {
        throw new UnsupportedOperationException("Not implemented, yet");
    }

    public Record topSickSite() {
        throw new UnsupportedOperationException("Not implemented, yet");
    }

    public List<Record> sickFrom(List<String> names) {
        throw new UnsupportedOperationException("Not implemented, yet");
    }
}
