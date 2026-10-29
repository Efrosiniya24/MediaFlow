# MediaFlow

## Table of Contents

- [1 Purpose](#1-purpose)
- [2 Data Model](#2-data-model)
    - [2.1 Files](#21-files)
        - [Physical files](#physical-files)
        - [File data](#file-data)

## 1 Purpose

Media processing (audio and video) without registration and authentication.

## 2 Data Model

### 2.1 Files

### Physical files

**Implemented:** local storage.     
**Future plans:** external storage.

The default permanent directory is `./data/storage`. The temporary directory is `./data/temp`.

```text
{permanent-dir}/
└── yyyy-MM-dd/
    └── {originFileId}/
        ├── {originFileId}_{fileName}
        └── {fileId}_{fileName}
```

Example

```text
./data/storage/
└── 2026-10-27/
    └── 6f1c2a/
        ├── 6f1c2a_interview.mp4
        └── 9ab2e1_interview.mp4
```

### File data

**Storage:** Redis

**1 File data:**  
Hash:`media:${originFileId}`

| Field      | Value            |
|:-----------|:-----------------|
| `{fileId}` | JSON `MediaFile` |

#### MediaFile

| Field          | Description                                               | Type        |
|----------------|-----------------------------------------------------------|-------------|
| `id`           | File id. For the original file, it matches `originFileId` | `UUID`      |
| `originFileId` | Group Id. It matches the original file id                 | `UUID`      |
| `originName`   | Original file name                                        | `String`    |
| `path`         | File path in permanent storage                            | `String`    |
| `mediaType`    | Media type: `VIDEO` or `AUDIO`                            | `MediaType` |
| `contentType`  | MIME type                                                 | `String`    |
| `duration`     | Duration, seconds                                         | `Double`    |
| `sizeBytes`    | File size, bytes                                          | `Long`      |
| `format`       | Container format                                          | `String`    |
| `width`        | Width                                                     | `Integer`   |
| `height`       | Height                                                    | `Integer`   |
| `videoCodec`   | Video codec                                               | `String`    |
| `audioCodec`   | Audio codec                                               | `String`    |
| `createdAt`    | Time when the record was saved                            | `Instant`   |

**2 Group lifetime**  
Sorted Set: `media:expirations`

| Member           | Score                            |
|------------------|----------------------------------|
| `{originFileId}` | Unix time when the lifetime ends |