package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservation.Reservation;
import roomescape.dto.reservation.MyReservationResponse;
import roomescape.dto.reservation.ReservationFilterParam;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.repository.ReservationRepository;
import roomescape.repository.WaitingRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ReservationService {

    private static final int MAX_RESERVATIONS_PER_TIME = 1;

    private final ReservationRepository reservationRepository;
    private final WaitingRepository waitingRepository;

    public ReservationService(final ReservationRepository reservationRepository, final WaitingRepository waitingRepository) {
        this.reservationRepository = reservationRepository;
        this.waitingRepository = waitingRepository;
    }

    public ReservationResponse create(final Reservation reservation) {
        validateDate(reservation.getDate());

        final int count = reservationRepository.countByDateAndTime_IdAndTheme_Id(
                reservation.getDate(), reservation.getReservationTimeId(), reservation.getThemeId()
        );
        validateDuplicatedReservation(count);
        return ReservationResponse.from(reservationRepository.save(reservation));
    }

    private void validateDate(final LocalDate date) {
        if (LocalDate.now().isAfter(date) || LocalDate.now().equals(date)) {
            throw new IllegalArgumentException("이전 날짜 혹은 당일은 예약할 수 없습니다.");
        }
    }

    private void validateDuplicatedReservation(final int count) {
        if (count >= MAX_RESERVATIONS_PER_TIME) {
            throw new IllegalArgumentException("해당 시간대에 예약이 모두 찼습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAll() {
        final List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAllBy(final ReservationFilterParam filterParam) {
        final List<Reservation> reservations = reservationRepository.findByTheme_IdAndMember_IdAndDateBetween(
                filterParam.themeId(), filterParam.memberId(), filterParam.dateFrom(), filterParam.dateTo()
        );
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public void delete(final Long id) {
        final boolean isExist = reservationRepository.existsById(id);
        if (!isExist) {
            throw new IllegalArgumentException("해당 ID의 예약이 없습니다.");
        }
        reservationRepository.deleteById(id);
    }

    public List<MyReservationResponse> findMyReservations(final Long id) {
        final List<Reservation> reservations = reservationRepository.findByMember_Id(id);
        return reservations.stream()
                .map(MyReservationResponse::from)
                .toList();
    }
}
