package tech.zoomidsoon.pickme_restful_api.repos;

import tech.zoomidsoon.pickme_restful_api.models.Hobby;
import tech.zoomidsoon.pickme_restful_api.utils.DBContext;
import tech.zoomidsoon.pickme_restful_api.mappers.HobbyRowMapper;
import tech.zoomidsoon.pickme_restful_api.utils.Utils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import lombok.*;

public class HobbyRepository implements Repository<Hobby> {
    private static final Repository<Hobby> singleton = new HobbyRepository();

    private HobbyRepository() {
    }

    public static Repository<Hobby> getInstance() {
        return singleton;
    }

    @Override
    public Hobby create(Hobby entity) throws Exception {
        try (Connection connection = DBContext.getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(
                    // Insert hobby to database
                    "INSERT INTO tblHobby (hobbyname,Decription) VALUES (?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, entity.getHobbyName());
                stmt.setString(2, entity.getDecription());
                // Check that statement is excuted, if not return null
                if (stmt.executeUpdate() == 0)
                    return null;

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    rs.next();
                    entity.setHobbyName(rs.getString(1));
                    return entity;
                }
            }
        }
    }

    @Override
    public List<Hobby> read(Criteria criteria) throws Exception {
        try (Connection connection = DBContext.getConnection()) {
            try (ResultSet rs = criteria.query(connection)) {
                return HobbyRowMapper.getInstance().processResultSet(rs, Hobby.class);
            }
        }
    }

    @Override
    public Hobby update(Hobby entity) throws Exception {
        try (Connection connection = DBContext.getConnection()) {
            if (entity.isEmpty())
                return null;

            FindByHobbyId fid = new FindByHobbyId(entity.getHobbyName());
            List<Hobby> list = HobbyRowMapper.getInstance().processResultSet(fid.query(connection), Hobby.class);

            if (list.size() == 0)
                return null;

            Hobby inDB = list.get(0);

            Utils.copyNonNullFields(inDB, entity);

            try (PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE tblhobby SET decription = ? WHERE hobbyID = ?")) {
                stmt.setInt(2, inDB.getHobbyId());
                stmt.setString(1, inDB.getDecription());
                if (stmt.executeUpdate() > 0)
                    return inDB;
            }
        }
        return null;
    }

    @Override
    public Hobby delete(Hobby entity) throws Exception {
        try {
            try (Connection connection = DBContext.getConnection()) {
                FindByName fib = new FindByName(entity.getHobbyName());
                List<Hobby> list = HobbyRowMapper.getInstance().processResultSet(fib.query(connection), Hobby.class);

                if (list.size() == 0)
                    return null;

                Hobby user = list.get(0);

                try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM tblhobby WHERE hobbyname = ?")) {
                    stmt.setString(1, entity.getHobbyName());

                    if (stmt.executeUpdate() > 0)
                        return user;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public List<Hobby> readAll() throws Exception {
        try (Connection connection = DBContext.getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM tblhobby")) {
                try (ResultSet rs = stmt.executeQuery()) {
                    return HobbyRowMapper.getInstance().processResultSet(rs, Hobby.class);
                }
            }
        }
    }

    @AllArgsConstructor
    public static class FindByName implements Criteria {
        private String hobbyName;

        @Override
        public ResultSet query(Connection conn) throws Exception {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tblhobby WHERE hobbyname LIKE ?");
            stmt.setString(1, hobbyName);
            return stmt.executeQuery();
        }
    }
}
