package raido.apisvc.db;

import org.apache.jena.riot.RDFDataMgr;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class LoadSubjectsTask extends DefaultTask {
  private static final String ID_KEY = "notation";
  private static final String NAME_KEY = "prefLabel";
  private static final String DESCRIPTION_KEY = "definition";
  private static final String NOTE_KEY = "scopeNote";
  private static final String LANGUAGE_LABEL = "@en";

  private static final String CONCEPT_TYPE = "http://www.w3.org/2004/02/skos/core#Concept";
  @Input
  abstract Property<String> getDataFileUrl();

  @Input
  abstract Property<String> getDbUrl();

  @Input
  abstract Property<String> getDbUsername();

  @Input
  abstract Property<String> getDbPassword();

  @TaskAction
  void load() throws SQLException, ClassNotFoundException {
    final var forcodes = new HashMap<String, Map<String, String>>();
    final var model = RDFDataMgr.loadModel(getDataFileUrl().get());
    model.listStatements()
      .forEachRemaining(statement -> {
        final var subject = statement.getSubject().toString();

        if (!forcodes.containsKey(subject)) {
          forcodes.put(subject, new HashMap<>());
        }

        forcodes.get(subject).put(statement.getPredicate().getLocalName(), statement.getObject().toString());

      });

    insert(forcodes);
  }

  void insert(final Map<String, Map<String, String>> forcodes) throws ClassNotFoundException, SQLException {
    final var driverClassName = "org.postgresql.Driver";

    Class.forName(driverClassName);
    final var connection = DriverManager.getConnection(getDbUrl().get(), getDbUsername().get(), getDbPassword().get());

    forcodes.keySet().stream()
      .filter(key -> forcodes.get(key).get("type").equals(CONCEPT_TYPE))
      .forEach(key -> {
        final var forcode = forcodes.get(key);

        final var id = forcode.get(ID_KEY).replace(LANGUAGE_LABEL, "");
        final var name = forcode.get(NAME_KEY).replace(LANGUAGE_LABEL, "");
        final var description = forcode.containsKey(DESCRIPTION_KEY) ?
          forcode.get(DESCRIPTION_KEY).replace(LANGUAGE_LABEL, "") : null;

        final var note = forcode.containsKey(NOTE_KEY) ?
          forcode.get(NOTE_KEY).replace(LANGUAGE_LABEL, "") : null;


        final var columns = new ArrayList<>(Arrays.asList("id", "name"));
        final var values = new ArrayList<>(Arrays.asList(id, name));
        final var placeholders = new ArrayList<>(Arrays.asList("?", "?"));

        if (description != null) {
          columns.add("description");
          values.add(description);
          placeholders.add("?");
        }
        if (note != null) {
          columns.add("note");
          values.add(note);
          placeholders.add("?");
        }

        final var query = String.format("insert into raido.api_svc.subject (" +
          String.join(",", columns) + ") values (" + String.join(",", placeholders) + ")");

        final PreparedStatement statement;
        try {
          statement = connection.prepareStatement(query);

          for (int i = 1; i <= values.size(); i++) {
            statement.setString(i, values.get(i - 1));
          }

          statement.execute();
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
      });
  }
}
