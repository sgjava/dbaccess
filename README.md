## DB Access

[DB Access](http://sgjava.github.io/dbaccess) is a database abstraction layer
that prevents coding to a specific implementation such as Apache DBUtils or
Spring JdbcTemplate. This allows you to swap out the implementation as needed
without impacting dependant code. DB Access has the following features:
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
* No explicit ORM mappings are required since DB Access follows simple field
level mapping rules. Database field names with underscores are converted to
camelCase and mapped to bean fields. Of course you have the choice of returning
a List of beans or List&lt;Map&lt;String, Object&gt;&gt; which can be easily
rendered as a JSON object. The DbUtils implementation adds a BeanProcessor which
allows DbUtils to handle mapping the same way.
* Method level transactions can be added with a simple @Transaction annotation.
This includes thread based transactions using a DataSource or Connection and XA
data sources. Ultra fast connection based transactions can also be substituted
by simply selecting a different transaction module from TransactionFactory.
* For JDBC drivers that support RETURN_GENERATED_KEYS all implementations
automatically support returning keys with updateReturnKeys or returning a
single key value with updateReturnKey.
