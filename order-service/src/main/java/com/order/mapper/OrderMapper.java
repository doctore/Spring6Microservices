package com.order.mapper;

import com.order.configuration.persistence.PersistenceConfiguration;
import com.order.model.Order;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

@Mapper
public interface OrderMapper {

    // Database table related with the model of the current mapper
    String TABLE = PersistenceConfiguration.SCHEMA + "." + "order";

    // Columns defined in the table of database
    String ID_COLUMN = "id";
    String CODE_COLUMN = "code";
    String CREATED_AT_COLUMN = "created_at";

    // Used to know how to manage raw results from database
    String ORDER_RESULT_MAP_IDENTIFIER = "OrderResultMap";
    String ORDER_RESULT_MAP_METHOD = "com.order.mapper.OrderMapper.findById";


    @Select("SELECT * "
          + "FROM " + TABLE
          + " WHERE " + ID_COLUMN + " = #{id}"
    )
    @Results(
            id = ORDER_RESULT_MAP_IDENTIFIER,
            value = {
                    @Result(
                            property = "id",
                            column = ID_COLUMN
                    ),
                    @Result(
                            property = "code",
                            column = CODE_COLUMN
                    ),
                    @Result(
                            property = "createdAt",
                            column = CREATED_AT_COLUMN
                    ),
                    @Result(
                            // Variable name in Order to map onto
                            property = "orderLines",
                            // Column data that should be passed to the method referenced below
                            column = OrderLineMapper.ID_COLUMN,
                            // The type of the variable referenced above
                            javaType = List.class,
                            // There could be several OrderLines for each Order
                            many = @Many(
                                    // Reference the method in the OrderLineMapper class that will return
                                    // the orderLines based on the data the column specified above contains
                                    select = OrderLineMapper.ORDER_LINE_RESULT_MAP_METHOD,
                                    // Lazy loaded of OrderLine
                                    fetchType = FetchType.LAZY
                            )
                    )
            }
    )
    Order findById(Integer id);


    @Select("SELECT * "
            + "FROM " + TABLE
            + " WHERE " + CODE_COLUMN + " = #{code}"
    )
    @ResultMap(ORDER_RESULT_MAP_IDENTIFIER)
    Order findByCode(final String code);


    @Delete("DELETE FROM " + TABLE
          + " WHERE " + ID_COLUMN + " = #{id}"
    )
    void deleteById(final Integer id);


    @Delete("DELETE FROM " + TABLE
          + " WHERE " + CODE_COLUMN + " = #{code}"
    )
    void deleteByCode(final String code);


    @Insert("INSERT INTO " + TABLE + " ("
               + CODE_COLUMN
               + ", " + CREATED_AT_COLUMN
            + ") "
            + "VALUES ("
               + "#{code}"
               + ", #{createdAt} "
            + ")"
    )
    @Options(
            useGeneratedKeys = true,
            keyColumn = ID_COLUMN,
            keyProperty = ID_COLUMN
    )
    void insert(final Order order);


    @Update("UPDATE " + TABLE
          + " SET "
               + CODE_COLUMN + " = #{code} "
          + "WHERE id = #{id}"
    )
    void update(final Order order);

}
