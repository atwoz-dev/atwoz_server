package atwoz.atwoz.payment.infra;

import atwoz.atwoz.payment.domain.TokenParser;
import atwoz.atwoz.payment.domain.TransactionInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SignedTransactionInfoParser implements TokenParser {
    private final ObjectMapper objectMapper;

    public TransactionInfo parseToTransactionInfo(String signedTransactionInfo) {
        Claims claims = parseClaims(signedTransactionInfo);
        TransactionInfo transactionInfo = objectMapper.convertValue(claims, TransactionInfo.class);
        return transactionInfo;
    }

    private Jws<Claims> parseJws(String token) {
        return Jwts.parserBuilder()
                .build()
                .parseClaimsJws(token);
    }

    private Claims parseClaims(String token) {
        return parseJws(token).getBody();
    }
}
