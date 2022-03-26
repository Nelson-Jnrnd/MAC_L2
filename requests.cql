// Requête 1 - possibleSpreaders
MATCH (p1:Person {healthstatus:"Sick"})-[visit1:VISITS]->(:Place)<-[visit2:VISITS]-(p2:Person {healthstatus:"Healthy"})
WHERE visit1.starttime > p1.confirmedtime AND visit2.starttime > p1.confirmedtime
RETURN DISTINCT p1.name AS sickName

// Requête 2 - possibleSpreadCounts
MATCH (p1:Person {healthstatus:"Sick"})-[visit1:VISITS]->(:Place)<-[visit2:VISITS]-(p2:Person {healthstatus:"Healthy"})
WHERE visit1.starttime > p1.confirmedtime AND visit2.starttime > p1.confirmedtime
RETURN p1.name AS sickName, COUNT(p2) AS nbHealthy

// Requête 3 - carelessPeople
MATCH (person:Person {healthstatus:"Sick"})-[:VISITS]->(place:Place)
WITH person, count(place.name) AS nbPlaces
WHERE nbPlaces > 10
RETURN person.name AS sickName, nbPlaces ORDER BY nbPlaces DESC

// Requête 4 - sociallyCareful
MATCH (person:Person {healthstatus:"Sick"})-[visit:VISITS]-(place:Place {type:"Bar"})
WHERE all(_ in [person.confirmedtime, visit.starttime] WHERE visit.starttime < person.confirmedtime)
RETURN DISTINCT(person.name) AS sickName

// Requête 5 - peopleToInform
match ()-[:ACTED_]-

// Requête 6 - setHighRisk
match ()-[:ACTED_]-

// Requête 7 - healthyCompanionsOf
MATCH (n:Person{name:"Payton Kent"})-[:VISITS*1..3]-(v:Person{healthstatus:"Healthy"})
RETURN v as healthyName

// Requête 8 - topSickSite
MATCH (p:Place)-[:VISITS]-(s:Person{healthstatus:"Sick"})
RETURN p.type AS placeType, size(collect(s)) AS nbOfSickVisits ORDER BY nbOfSickVisits DESC LIMIT 1

// Requête 9 - sickFrom
MATCH (n:Person {healthstatus:"Sick"})
RETURN n AS sickName
