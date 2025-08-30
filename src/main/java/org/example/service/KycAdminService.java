package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.KycProcess;
import org.example.repository.KycProcessRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KycAdminService {

    private final KycProcessRepository kycProcessRepository;

    public Page<KycProcess> getKycList(Pageable pageable, String status) {
        if ("ALL".equals(status)) {
            return kycProcessRepository.findAll(pageable);
        } else {
            KycProcess.KycStatus kycStatus = KycProcess.KycStatus.valueOf(status);
            return kycProcessRepository.findByStatus(kycStatus, pageable);
        }
    }

    public KycProcess getKycDetail(String kycId) {
        Optional<KycProcess> processOpt = kycProcessRepository.findById(kycId);
        if (!processOpt.isPresent()) {
            throw new RuntimeException("KYC process not found: " + kycId);
        }
        return processOpt.get();
    }

    public void deleteKyc(String kycId) {
        if (!kycProcessRepository.existsById(kycId)) {
            throw new RuntimeException("KYC process not found: " + kycId);
        }
        kycProcessRepository.deleteById(kycId);
        log.info("KYC process deleted: {}", kycId);
    }
}