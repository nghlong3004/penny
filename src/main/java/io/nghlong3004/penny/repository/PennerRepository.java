package io.nghlong3004.penny.repository;

import io.nghlong3004.penny.model.Penner;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PennerRepository {
    @Insert("""
            INSERT INTO penner (chat_id, first_name, last_name, status)
            VALUES (#{chatId}, #{firstName}, #{lastName}, #{status}::penner_status);
            """)
    void insert(Penner penner);

    @Update("""
            UPDATE penner
            SET first_name = #{firstName},
                last_name  = #{lastName},
                status     = #{status}::penner_status,
                updated    = NOW()
            WHERE chat_id  = #{chatId};
            """)
    void update(Penner penner);

    @Delete("""
            DELETE
            FROM penner
            WHERE chat_id = #{chatId};
            """)
    void deletePennerByChatId(Long chatId);

    @Select("""
            SELECT * FROM penner
            WHERE chat_id = #{chatId};
            """)
    Optional<Penner> getPennerByChatId(Long chatId);

    @Select("""
            SELECT *
            FROM penner;
            """)
    Optional<List<Penner>> getAllPenner();
}
