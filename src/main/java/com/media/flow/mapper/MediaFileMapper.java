package com.media.flow.mapper;

import com.media.flow.dto.MediaFileDto;
import com.media.flow.model.MediaFile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 02.08.2026
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MediaFileMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "originFileId", ignore = true)
    @Mapping(target = "originName", ignore = true)
    @Mapping(target = "path", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    MediaFile toEntity(MediaFileDto dto);

    @Mapping(source = "mediaFile.originName", target = "fileName")
    MediaFileDto toDto(MediaFile mediaFile, LocalDateTime expiresAt);
}
