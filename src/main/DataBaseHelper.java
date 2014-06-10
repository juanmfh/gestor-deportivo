package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author JuanM
 */
public class DataBaseHelper {

    private Connection connection;
    private Statement s;
    private static final int DATABASEEXIST = 20000;

    public DataBaseHelper() throws SQLException {

        connection = DriverManager.getConnection("jdbc:derby:appDeportivaDB;create=true", "app", "app");
        s = connection.createStatement();
    }

    public void iniDB() throws SQLException {
        try {
            connection.setAutoCommit(false);

            s.execute("CREATE table USUARIO (\n"
                    + "    id          INTEGER NOT NULL \n"
                    + "                PRIMARY KEY GENERATED ALWAYS AS IDENTITY \n"
                    + "                (START WITH 1, INCREMENT BY 1),\n"
                    + "    nick    VARCHAR(30) NOT NULL UNIQUE, \n"
                    + "    password   VARCHAR(30) NOT NULL\n"
                    + ")");
            s.execute("CREATE table COMPETICION (\n"
                    + "    id          INTEGER NOT NULL \n"
                    + "                PRIMARY KEY GENERATED ALWAYS AS IDENTITY \n"
                    + "                (START WITH 1, INCREMENT BY 1),\n"
                    + "    nombre      VARCHAR(80) NOT NULL UNIQUE , \n"
                    + "    ciudad      VARCHAR(80),\n"
                    + "    pais        VARCHAR(80),\n"
                    + "    fechaInicio DATE,\n"
                    + "    fechaFin    DATE,\n"
                    + "    organizador VARCHAR(80),\n"
                    + "	imagen      VARCHAR(400)\n"
                    + ")");
            s.execute("CREATE table PRUEBA (\n"
                    + "    id          INTEGER NOT NULL \n"
                    + "                PRIMARY KEY GENERATED ALWAYS AS IDENTITY \n"
                    + "                (START WITH 1, INCREMENT BY 1),\n"
                    + "    nombre      VARCHAR(80) NOT NULL, \n"
                    + "    tipo        VARCHAR(80) NOT NULL, \n"
                    + "    tiporesultado VARCHAR(80) NOT NULL\n"
                    + ")");
            s.execute("CREATE table ADMINISTRADO (\n"
                    + "    id          INTEGER NOT NULL \n"
                    + "                PRIMARY KEY GENERATED ALWAYS AS IDENTITY \n"
                    + "                (START WITH 1, INCREMENT BY 1),\n"
                    + "    competicion_id      INTEGER NOT NULL, \n"
                    + "    usuario_id          INTEGER NOT NULL,\n"
                    + "    FOREIGN KEY(competicion_id) REFERENCES COMPETICION(id),\n"
                    + "    FOREIGN KEY(usuario_id)     REFERENCES USUARIO(id)\n"
                    + ")");
            s.execute("CREATE table COMPUESTA (\n"
                    + "    id          INTEGER NOT NULL \n"
                    + "                PRIMARY KEY GENERATED ALWAYS AS IDENTITY \n"
                    + "                (START WITH 1, INCREMENT BY 1),\n"
                    + "    competicion_id      INTEGER NOT NULL, \n"
                    + "    prueba_id           INTEGER NOT NULL,\n"
                    + "    orden               INTEGER NOT NULL,\n"
                    + "    FOREIGN KEY(competicion_id) REFERENCES COMPETICION(id),\n"
                    + "    FOREIGN KEY(prueba_id)     REFERENCES PRUEBA(id)\n"
                    + ")");
            s.execute("CREATE table GRUPO (\n"
                    + "    id          INTEGER NOT NULL \n"
                    + "                PRIMARY KEY GENERATED ALWAYS AS IDENTITY \n"
                    + "                (START WITH 1, INCREMENT BY 1),\n"
                    + "    nombre          VARCHAR(80),\n"
                    + "    grupo_id        INTEGER,\n"
                    + "    FOREIGN KEY(grupo_id) REFERENCES GRUPO(id)\n"
                    + ")");
            s.execute("CREATE table ACCESO (\n"
                    + "    id          INTEGER NOT NULL \n"
                    + "                PRIMARY KEY GENERATED ALWAYS AS IDENTITY \n"
                    + "                (START WITH 1, INCREMENT BY 1),\n"
                    + "    usuario_id      INTEGER NOT NULL,\n"
                    + "    grupo_id        INTEGER NOT NULL,\n"
                    + "    FOREIGN KEY(usuario_id)     REFERENCES USUARIO(id),\n"
                    + "    FOREIGN KEY(grupo_id) REFERENCES GRUPO(id)\n"
                    + ")");
            s.execute("CREATE table INSCRIPCION (\n"
                    + "    id          INTEGER NOT NULL \n"
                    + "                PRIMARY KEY GENERATED ALWAYS AS IDENTITY \n"
                    + "                (START WITH 1, INCREMENT BY 1),\n"
                    + "    fecha           DATE,\n"
                    + "    grupo_id        INTEGER NOT NULL,\n"
                    + "    competicion_id  INTEGER NOT NULL,\n"
                    + "    FOREIGN KEY(competicion_id) REFERENCES COMPETICION(id),\n"
                    + "    FOREIGN KEY(grupo_id) REFERENCES GRUPO(id)\n"
                    + ")");
            s.execute("CREATE table EQUIPO (\n"
                    + "     id          INTEGER NOT NULL \n"
                    + "                 PRIMARY KEY GENERATED ALWAYS AS IDENTITY \n"
                    + "                 (START WITH 1, INCREMENT BY 1),\n"
                    + "     nombre         VARCHAR(80) NOT NULL,\n"
                    + "     grupo_id	INTEGER NOT NULL,\n"
                    + "     FOREIGN KEY(grupo_id) REFERENCES GRUPO(id)\n"
                    + ")");
            s.execute("CREATE table PARTICIPANTE (\n"
                    + "     id              INTEGER NOT NULL \n"
                    + "                     PRIMARY KEY GENERATED ALWAYS AS IDENTITY \n"
                    + "                     (START WITH 1, INCREMENT BY 1),\n"
                    + "     equipo_id       INTEGER,\n"
                    + "     grupo_id        INTEGER NOT NULL,\n"
                    + "     apellidos       VARCHAR(80) NOT NULL,\n"
                    + "     nombre          VARCHAR(80) NOT NULL,\n"
                    + "     edad            INTEGER,\n"
                    + "     sexo            INTEGER,\n"
                    + "     dorsal          INTEGER NOT NULL,\n"
                    + "     pruebaAsignada	INTEGER,\n"
                    + "     FOREIGN KEY(grupo_id) REFERENCES GRUPO(id),\n"
                    + "     FOREIGN KEY(equipo_id) REFERENCES EQUIPO(id),\n"
                    + "     FOREIGN KEY(pruebaAsignada) REFERENCES PRUEBA(id)\n"
                    + ")");
            s.execute("CREATE table REGISTRO (\n"
                    + "     id              INTEGER NOT NULL \n"
                    + "                     PRIMARY KEY GENERATED ALWAYS AS IDENTITY \n"
                    + "                     (START WITH 1, INCREMENT BY 1),\n"
                    + "     tiempo          TIMESTAMP,\n"
                    + "     num             FLOAT,\n"
                    + "     inscripcion_id  INTEGER NOT NULL,\n"
                    + "     prueba_id       INTEGER NOT NULL,\n"
                    + "     participante_id         INTEGER,\n"
                    + "     equipo_id               INTEGER,\n"
                    + "     num_intento     INTEGER NOT NULL,\n"
                    + "     sorteo          INTEGER NOT NULL,\n"
                    + "     FOREIGN KEY(inscripcion_id) REFERENCES INSCRIPCION(id),\n"
                    + "     FOREIGN KEY(participante_id) REFERENCES PARTICIPANTE(id),\n"
                    + "     FOREIGN KEY(prueba_id) REFERENCES PRUEBA(id),\n"
                    + "	FOREIGN KEY(equipo_id) REFERENCES EQUIPO(id)\n"
                    + ")");
            s.execute("INSERT INTO USUARIO (nick, password) VALUES ('admin', 'admin')");

        } catch (SQLException ex) {
            if (ex.getErrorCode() != DATABASEEXIST) {
                throw new SQLException(ex);
            }
        } finally {
            connection.commit();
        }
    }

    public void close() throws SQLException {

        s.close();
        connection.close();
    }

}
