package com.ogu.ogoods.repository;

import com.ogu.ogoods.dto.MainNoticeDTO;
import com.ogu.ogoods.dto.NoticeSearchDTO;
import com.ogu.ogoods.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticeRepositoryCustom {

    Page<Notice> getAdminNoticePage(NoticeSearchDTO noticeSearchDTO, Pageable pageable);

    Page<MainNoticeDTO> getMainNoticePage(NoticeSearchDTO noticeSearchDTO, Pageable pageable);
}
