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
MATCH (sick:Person {healthstatus:"Sick"})-[visit:VISITS]-(:Place {type:"Bar"})
WITH sick, collect(visit) AS visits 
WHERE all(visit IN visits WHERE sick.confirmedtime > visit.endtime)
RETURN sick.name AS sickName
UNION ALL
MATCH (sick2:Person {healthstatus:"Sick"})
WHERE NOT exists((sick2)-[:VISITS]-(:Place {type:"Bar"}))
RETURN sick2.name AS sickName

// Requête 5 - peopleToInform :::: Je sais pas si le distinct dans le collect est de trop. Mais mieux vaut trop que pas assez
MATCH (sick:Person {healthstatus:"Sick"})-[v:VISITS]-(p:Place)-[v2:VISITS]-(potential:Person {healthstatus:"Healthy"})
WHERE duration.inSeconds(apoc.coll.max([v.starttime, v2.starttime]), apoc.coll.min([v.endtime, v2.endtime])).hours >= 2
RETURN sick.name AS sickName, collect(DISTINCT (potential.name)) AS peopleToInform

// Requête 6 - setHighRisk ::::::::: C'est pas bon, faudrait que ça retourne tout les high risk même ceux qui ont pas été modifié par cette requête. Aled :'(
MATCH (sick:Person {healthstatus:"Sick"})-[v:VISITS]-(p:Place)-[v2:VISITS]-(potential:Person {healthstatus:"Healthy"})
WHERE duration.inSeconds(apoc.coll.max([v.starttime, v2.starttime]), apoc.coll.min([v.endtime, v2.endtime])).hours >= 2
SET potential.risk = "High"
RETURN DISTINCT potential.name as highRiskName

// Requête 7 - healthyCompanionsOf
MATCH (n:Person{name:$name})-[:VISITS*1..3]-(v:Person{healthstatus:"Healthy"})
RETURN v.name AS healthyName

// Requête 8 - topSickSite
MATCH (p:Place)-[:VISITS]-(s:Person{healthstatus:"Sick"})
RETURN p.type AS placeType, size(collect(s)) AS nbOfSickVisits ORDER BY nbOfSickVisits DESC LIMIT 1

// Requête 9 - sickFrom
MATCH (n:Person {healthstatus:"Sick"})
where n.name in $names
RETURN n.name AS sickName
