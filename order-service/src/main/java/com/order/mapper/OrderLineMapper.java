package com.order.mapper;

import com.order.configuration.persistence.PersistenceConfiguration;
import com.order.model.Order;
import com.order.model.OrderLine;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

@Mapper
public interface OrderLineMapper {

    // Database table related with the model of the current mapper
    String TABLE = PersistenceConfiguration.SCHEMA + "." + "order_line";

    // Columns defined in the table of database
    String ID_COLUMN = "id";
    String ORDER_COLUMN = "order_id";
    String CONCEPT_COLUMN = "concept";
    String AMOUNT_COLUMN = "amount";
    String COST_COLUMN = "cost";

    // Used to know how to manage raw results from database
    String ORDER_LINE_RESULT_MAP_IDENTIFIER = "OrderLineResultMap";
    String ORDER_LINE_RESULT_MAP_METHOD = "com.order.mapper.OrderLineMapper.findByOrderId";


    @Select("SELECT count(*) "
          + "FROM " + TABLE
    )
    long count();


    @Delete("DELETE FROM " + TABLE
          + " WHERE " + ID_COLUMN + " = #{id}"
    )
    int deleteById(final Integer id);


    @Delete("DELETE FROM " + TABLE
          + " WHERE " + ORDER_COLUMN + " = #{orderId}"
    )
    int deleteByOrderId(final Integer orderId);


    @Select("SELECT * "
          + "FROM " + TABLE
          + " WHERE " + ID_COLUMN + " = #{id}"
    )
    @Results(
            id = ORDER_LINE_RESULT_MAP_IDENTIFIER,
            value = {
                    @Result(
                            property = "id",
                            column = ID_COLUMN
                    ),
                    @Result(
                            // Variable name in OrderLine to map onto
                            property = "order",
                            // Column data that should be passed to the method referenced below
                            column = ORDER_COLUMN,
                            // The type of the variable referenced above
                            javaType = Order.class,
                            // There is only a single Order for each OrderLine
                            one = @One(
                                    // Reference the method in the OrderMapper class that will return
                                    // an order based on the data the column specified above contains
                                    select = OrderMapper.ORDER_RESULT_MAP_METHOD,
                                    // Lazy loaded of Order
                                    fetchType = FetchType.LAZY
                            )
                    ),
                    @Result(
                            property = "concept",
                            column = CONCEPT_COLUMN
                    ),
                    @Result(
                            property = "amount",
                            column = AMOUNT_COLUMN
                    ),
                    @Result(
                            property = "cost",
                            column = COST_COLUMN
                    )
            }
    )
    OrderLine findById(final Integer id);


    @Select("SELECT * "
          + "FROM " + TABLE
          + " WHERE " + CONCEPT_COLUMN + " = #{concept}"
    )
    @ResultMap(ORDER_LINE_RESULT_MAP_IDENTIFIER)
    List<OrderLine> findByConcept(final String concept);


    @Select("SELECT * "
          + "FROM " + TABLE
          + " WHERE " + ORDER_COLUMN + " = #{orderId}"
    )
    @ResultMap(ORDER_LINE_RESULT_MAP_IDENTIFIER)
    List<OrderLine> findByOrderId(final Integer orderId);


    @Insert("INSERT INTO " + TABLE + " ("
               + ORDER_COLUMN
               + ", " + CONCEPT_COLUMN
               + ", " + AMOUNT_COLUMN
               + ", " + COST_COLUMN
          + ") "
          + "VALUES ("
               + "#{order.id} "
               + ", #{concept} "
               + ", #{amount} "
               + ", #{cost} "
          + ")"
    )
    @Options(
            useGeneratedKeys = true,
            keyColumn = ID_COLUMN,
            keyProperty = ID_COLUMN
    )
    int insert(final OrderLine orderLine);


    @Update("UPDATE " + TABLE
          + " SET "
               + CONCEPT_COLUMN + " = #{concept} "
               + ", " + AMOUNT_COLUMN + " = #{amount} "
               + ", " + COST_COLUMN + " = #{cost} "
          + "WHERE id = #{id}"
    )
    int update(final OrderLine orderLine);

}
