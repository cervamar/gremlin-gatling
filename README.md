# gremlin-gatling

This project is an extension to [Gatling](https://gatling.io/docs/current/quickstart/). It adds support for gremlin as a new protocol.

## General

In order to use the Gremlin functionality, your simulation has to import `import cz.cvut.fit.cervamar.gatling.GremlinPredef._`
```
val gremlinProtocol = new GremlinProtocol("src/main/resources/remote.yaml")
```
To define Gremlin protocol, you have to create instance of GremlinProtocol, which accepts path to config file, which is in most cases same as the official [remote.yaml](https://github.com/apache/tinkerpop/blob/master/gremlin-console/conf/remote.yaml) used in Gremlin Console.
Configuration defines connection to an instance of Gremlin server, but it might be cloud storage as well. See [Azure Cosmo DB](https://docs.microsoft.com/en-us/azure/cosmos-db/create-graph-gremlin-console). 
```
scenario("Gremlin Example")
    .exec(gremlin("getVertex")
      .query("g.V().has('name','marko')")
```

The entry point for the operations is the gremlin() method. The method itself takes a request name as parameter. This name will appear in the reports to represent the operation that follows.
Method gremlin().query() takes a query as parameter. Supported query language is Gremlin-Groovy, check [Tinkerpop official documentation](http://tinkerpop.apache.org/docs/3.2.3/reference/) for more information.

All standard functionality from Gremlin remains, so you can use session variables in queries:
```
    .exec(gremlin("getVertex")
      .query("g.V().has('name','${nameValue}')")
```

You can check result of query:
```
    .exec(gremlin("getVertex")
      .query("g.V().has('name','${nameValue}')")
      .simpleCheck(res => res.size == 1)
```

You can save result of query to session:
```
    .exec(gremlin("getVertex")
      .query("g.V().count()')")
      .extractResultAndSaveAs(resul => res.head.getLong, "numberOfVertices")
      .exec{session =>
      	println(session("numberOfVertices").as[String])
      	session}
```

## Simulation execution
Project is a maven project. In order to execute simulation, run maven compile and test jobs,  
then use gatling:test. See [Gatling maven plugin](https://gatling.io/docs/2.3/extensions/maven_plugin/) documentation.

Default simulation is GremlinModernSimulation, you can change that in pom.xml. This simulation refers to data in TinkerGraph modern graph variant.
Make sure that Gremlin Server is running, you can run cz.cvut.fit.cervamar.gremlin.server.EmbeddedServer, which starts Gremlin server with TinkerGraph modern graph.


