package roomescape.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.reservation.ReservationTime;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.TestFixture.RESERVATION_TIME_SEVEN;
import static roomescape.TestFixture.RESERVATION_TIME_SIX;

class ReservationTimeRepositoryTest extends RepositoryTest {

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    private ReservationTime reservationTime;

    @BeforeEach
    void setUp() {
       reservationTime = reservationTimeRepository.save(RESERVATION_TIME_SEVEN());
    }

    @Test
    @DisplayName("예약 시간을 저장한다.")
    void save() {
        // given
        final ReservationTime reservationTime = RESERVATION_TIME_SIX();

        // when
        final ReservationTime actual = reservationTimeRepository.save(reservationTime);

        // when
        assertThat(actual.getId()).isNotNull();
    }

    @Disabled
    @Test
    @DisplayName("예약 시간 목록을 조회한다.")
    void findAll() {
        // when
        final List<ReservationTime> actual = reservationTimeRepository.findAll();

        // then
        assertThat(actual).hasSize(1);
    }

    @Test
    @DisplayName("Id로 예약 시간을 조회한다.")
    void findById() {
        // when
        final Optional<ReservationTime> actual = reservationTimeRepository.findById(reservationTime.getId());

        // then
        assertThat(actual).isPresent();
    }

    @Test
    @DisplayName("존재하지 않는 Id로 예약 시간을 조회하면 빈 옵션을 반환한다.")
    void returnEmptyOptionalWhenFindByNotExistingId() {
        // given
        final Long notExistingId = 0L;

        // when
        final Optional<ReservationTime> actual = reservationTimeRepository.findById(notExistingId);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("Id로 예약 시간을 삭제한다.")
    void deleteById() {
        // when
        reservationTimeRepository.deleteById(reservationTime.getId());

        // then
        final List<ReservationTime> actual = reservationTimeRepository.findAll();
        assertThat(actual).doesNotContain(reservationTime);
    }
}