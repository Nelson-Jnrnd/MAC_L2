package ch.heig.mac;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;

public class Requests {
    private static final Logger LOGGER = Logger.getLogger(Requests.class.getName());
    private final Driver driver;

    private List<Record> fetchRecordsFromQuery(String query) {
        try (Session session = driver.session()) {
            Result result = session.run(query);
            return result.list();
        }
    }

    private List<Record> fetchRecordsFromParameterizedQuery(String query, Map<String, Object> parameters) {
        try (Session session = driver.session()) {
            Result result = session.run(query, parameters);
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

        return fetchRecordsFromQuery(query);
    }

    public List<Record> possibleSpreadCounts() {
        String query = "MATCH (p1:Person {healthstatus:\"Sick\"})-[visit1:VISITS]->(:Place)<-[visit2:VISITS]-(p2:Person {healthstatus:\"Healthy\"})\n" +
                "WHERE visit1.starttime > p1.confirmedtime AND visit2.starttime > p1.confirmedtime\n" +
                "RETURN p1.name as sickName, COUNT(p2) AS nbHealthy";

        return fetchRecordsFromQuery(query);
    }

    public List<Record> carelessPeople() {
        String query = "MATCH (person:Person {healthstatus:\"Sick\"})-[:VISITS]->(place:Place)\n" +
                "WITH person, count(place.name) AS nbPlaces\n" +
                "WHERE nbPlaces > 10\n" +
                "RETURN person.name AS sickName, nbPlaces ORDER BY nbPlaces DESC";

        return fetchRecordsFromQuery(query);
    }

    public List<Record> sociallyCareful() {
        String query = "MATCH (sick:Person {healthstatus:\"Sick\"})-[visit:VISITS]-(:Place {type:\"Bar\"})\n" +
                "WITH sick, collect(visit) AS visits \n" +
                "WHERE all(visit IN visits WHERE sick.confirmedtime > visit.endtime)\n" +
                "RETURN sick.name AS sickName\n" +
                "UNION ALL\n" +
                "MATCH (sick2:Person {healthstatus:\"Sick\"})\n" +
                "WHERE NOT exists((sick2)-[:VISITS]-(:Place {type:\"Bar\"}))\n" +
                "RETURN sick2.name AS sickName";

        return fetchRecordsFromQuery(query);
    }

    public List<Record> peopleToInform() {
        String query = "MATCH (sick:Person {healthstatus:\"Sick\"})-[v:VISITS]-(p:Place)-[v2:VISITS]-(potential:Person {healthstatus:\"Healthy\"})\n" +
                "WHERE duration.inSeconds(apoc.coll.max([v.starttime, v2.starttime]), apoc.coll.min([v.endtime, v2.endtime])).hours >= 2\n" +
                "RETURN sick.name AS sickName, collect(DISTINCT (potential.name)) AS peopleToInform";

        return fetchRecordsFromQuery(query);
    }

    public List<Record> setHighRisk() {
        String query = "MATCH (sick:Person {healthstatus:\"Sick\"})-[v:VISITS]-(p:Place)-[v2:VISITS]-(potential:Person {healthstatus:\"Healthy\"})\n" +
                "WHERE duration.inSeconds(apoc.coll.max([v.starttime, v2.starttime]), apoc.coll.min([v.endtime, v2.endtime])).hours >= 2\n" +
                "SET potential.risk = \"High\"\n" +
                "RETURN DISTINCT potential.name as highRiskName";

        return fetchRecordsFromQuery(query);
    }

    public List<Record> healthyCompanionsOf(String name) {
        String query = "MATCH (n:Person{name:$name})-[:VISITS*1..3]-(v:Person{healthstatus:\"Healthy\"})\n" +
                "RETURN v.name as healthyName";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", name);

        return fetchRecordsFromParameterizedQuery(query, parameters);
    }

    public Record topSickSite() {
        String query = "MATCH (p:Place)-[:VISITS]-(s:Person{healthstatus:\"Sick\"})\n" +
                "RETURN p.type AS placeType, size(collect(s)) AS nbOfSickVisits ORDER BY nbOfSickVisits DESC LIMIT 1";

        return fetchRecordsFromQuery(query).get(0);
    }

    public List<Record> sickFrom(List<String> names) {
        String query = "MATCH (n:Person {healthstatus:\"Sick\"})\n" +
                "where n.name in $names\n" +
                "RETURN n.name AS sickName";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("names", names);

        return fetchRecordsFromParameterizedQuery(query, parameters);
    }
}
