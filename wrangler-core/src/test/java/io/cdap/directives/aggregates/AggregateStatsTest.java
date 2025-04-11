@Test
public void testAggregateStats() {
    List<Row> rows = List.of(
        new Row().add("size", new ByteSize("1MB")).add("time", new TimeDuration("500ms")),
        new Row().add("size", new ByteSize("2MB")).add("time", new TimeDuration("1.5s"))
    );

    Directive directive = new AggregateStats();
    directive.initialize("size", "time", "total_size_mb", "total_time_sec");

    List<Row> results = directive.execute(rows);
    Assert.assertEquals(1, results.size());
    Assert.assertEquals(3, results.get(0).getValue("total_size_mb"));
    Assert.assertEquals(2, results.get(0).getValue("total_time_sec"));
}

