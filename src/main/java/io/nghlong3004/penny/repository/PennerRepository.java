package io.nghlong3004.penny.repository;

import io.nghlong3004.penny.model.Penner;
import io.nghlong3004.penny.model.type.PennerType;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PennerRepository {
    @Insert("""
            INSERT INTO penner (chat_id, first_name, last_name, spreadsheets_id, status)
            VALUES (#{chatId}, #{firstName}, #{lastName}, #{spreadsheetsId}, #{status}::penner_status);
            """)
    void insert(Penner penner);

    @Select("""
            SELECT spreadsheets_id FROM penner
            WHERE chat_id = #{chatId};
            """)
    String getSpreadsheetsId(Long chatId);

    @Update("""
            UPDATE penner
            SET first_name = #{firstName},
                last_name  = #{lastName},
                chat_id    = #{chatId},
                spreadsheets_id = #{spreadsheetsId},
                status     = #{status}::penner_status,
                updated    = NOW()
            WHERE chat_id  = #{chatId};
            """)
    void update(Penner penner);

    @Update("""
            UPDATE penner
            SET status     = #{status}::penner_status,
                updated    = NOW()
            WHERE chat_id  = #{chatId};
            """)
    void updateStatusByChatId(
            @Param("chatId") Long chatId,
            @Param("status") PennerType status);

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
    List<Penner> getAllPenner();
}
