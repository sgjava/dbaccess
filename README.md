## DB Access

DB Access is a database abstraction layer that prevents coding to a specific
implementation such as Apache DBUtils or Spring JdbcTemplate. This allows you to
swap out the implementation as needed without impacting dependant code. DB
Access has the following features:
* Common API currently supporting Apache DbUtils (DataSource and Connection
based), Spring JdbcTemplate, Persist and a home grown extension of the DbAccess
abstract class (DataSource and Connection based). Implementations can be easily
added by extending the DbAccess abstract class and writing the implementation.
* Minimal overhead is added compared to direct JDBC code or direct use of
underlying implementations. Even named parameters add almost no overhead.
* All implementations automatically support named parameters without using a
wrapped implementation like NamedParameterJdbcTemplate. DbAccess expects
parameter markers for Object[] parameters and named parameters for
Map&lt;String, Object&gt; parameters. You pick the strategy that best suits
your application. For batch operations Object[][] expects parameter markers and
List&lt;Map&lt;String, Object&gt;&gt; expects named parameters.
