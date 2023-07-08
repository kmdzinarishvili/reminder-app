package com.lineate.mdzinarishvili.reminderapp.dao;

import com.lineate.mdzinarishvili.reminderapp.enums.RoleType;
import com.lineate.mdzinarishvili.reminderapp.models.User;
import com.lineate.mdzinarishvili.reminderapp.models.UserMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoImpl  implements  UserDao{
    private final JdbcTemplate jdbcTemplate;

    private final String SQL_FIND_USER = "select  u.user_id, username,  email, password, role_name as role from users u " +
            " join roles r on r.role_id = u.role_id "+
            " where u.user_id = ?";
    private final String SQL_FIND_USER_BY_USERNAME =  "select  u.user_id, username,  email, password, role_name as role  from users u" +
            " join roles r on u.user_id = r.user_id " +
            " where username = ?";

    private final String SQL_DELETE_USER = "delete from users where user_id = ?";

    private final String SQL_UPDATE_USER = "update users set username = ?, email = ?, password  = ? where user_id = ?";
    private final String SQL_GET_ALL = "select u.user_id, username,  email, password, role_name as role from users u" +
            " join roles r on u.role_id = r.role_id";

    private final String SQL_INSERT_USER= "insert into users(username, email, password, role_id) values(?,?,?,?)";


    private final String SQL_FIND_USER_BY_EMAIL = "select  u.user_id, username,  email, password, role_name as role from users u " +
            " join roles r on r.role_id = u.role_id "+
            " where u.email = ?";

    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> selectUsers(){
        return jdbcTemplate.query(SQL_GET_ALL, new UserMapper());
    }
    public User save(User user) {
        int result = jdbcTemplate.update(SQL_INSERT_USER, user.getUsername(), user.getEmail(),
                user.getPassword(), findRoleId(user.getRole()));
        return this.findByEmail(user.getEmail()).get();
    }
    public Optional<User> findByEmail(String email){
        List<User> users =  jdbcTemplate.query(SQL_FIND_USER_BY_EMAIL, new UserMapper(), email);
        return users.stream().findFirst();
    }
    @Override
    public Optional<User>  selectUserById(Long id) {
        List<User> users =  jdbcTemplate.query(SQL_FIND_USER, new UserMapper(), id);
        return users.stream().findFirst();
    }

    @Override
    public Optional<User> selectUserByUsername(String username) {
        List<User> users =  jdbcTemplate.query(SQL_FIND_USER_BY_USERNAME, new UserMapper(), username);
        return users.stream().findFirst();
    }
    @Override
    public int deleteUserById(Long id) {
        return jdbcTemplate.update(SQL_DELETE_USER, id) ;
    }

    @Override
    public Optional<User> updateUser(User user) {
       jdbcTemplate.update(SQL_UPDATE_USER, user.getUsername(), user.getEmail(),
                user.getPassword(), user.getId()) ; // can't update role
        return this.selectUserById(user.getId());

    }

    private int findRoleId(RoleType roleType){
        final String SQL_FIND_RECURRENCE_TYPE = "select role_id from roles where role_name = ?";
        return  jdbcTemplate.query(SQL_FIND_RECURRENCE_TYPE, (rs, rowNum) -> rs.getInt("role_id"), roleType.toString()).stream().findFirst().orElseThrow();

    }

    @Override
    public Optional<User> insertUser(User user) {
        int result =  jdbcTemplate.update(SQL_INSERT_USER, user.getUsername(), user.getEmail(),
                user.getPassword(), findRoleId(user.getRole()));
        Long id = this.selectUserByUsername(user.getUsername()).get().getId();
        return this.selectUserById(id);

    }
}
