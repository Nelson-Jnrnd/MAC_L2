// Requête 1
MATCH (p1:Person {healthstatus:"Sick"})-[visit1:VISITS]->(:Place)<-[visit2:VISITS]-(p2:Person {healthstatus:"Healthy"})
WHERE visit1.starttime > p1.confirmedtime AND visit2.starttime > p1.confirmedtime
RETURN p1.name

// Requête 2
MATCH (p1:Person {healthstatus:"Sick"})-[visit1:VISITS]->(:Place)<-[visit2:VISITS]-(p2:Person {healthstatus:"Healthy"})
WHERE visit1.starttime > p1.confirmedtime AND visit2.starttime > p1.confirmedtime
RETURN p1.name, COUNT(p2) AS nbHealthy

// Requête 3
match ()-[:ACTED_]-

// Requête 4
match ()-[:ACTED_]-

// Requête 5
match ()-[:ACTED_]-

// Requête 6
match ()-[:ACTED_]-

// Requête 7
match ()-[:ACTED_]-

// Requête 8
match ()-[:ACTED_]-

// Requête 9
match ()-[:ACTED_]-
