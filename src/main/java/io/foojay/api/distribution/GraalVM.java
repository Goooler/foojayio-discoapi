/*
 * Copyright (c) 2023.
 *
 * This file is part of DiscoAPI.
 *
 *     DiscoAPI is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     DiscoAPI is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with DiscoAPI.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.foojay.api.distribution;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.hansolo.jdktools.Architecture;
import eu.hansolo.jdktools.ArchiveType;
import eu.hansolo.jdktools.Bitness;
import eu.hansolo.jdktools.HashAlgorithm;
import eu.hansolo.jdktools.OperatingSystem;
import eu.hansolo.jdktools.PackageType;
import eu.hansolo.jdktools.ReleaseStatus;
import eu.hansolo.jdktools.SignatureType;
import eu.hansolo.jdktools.TermOfSupport;
import eu.hansolo.jdktools.scopes.BuildScope;
import eu.hansolo.jdktools.util.OutputFormat;
import eu.hansolo.jdktools.versioning.Semver;
import eu.hansolo.jdktools.versioning.VersionNumber;
import io.foojay.api.CacheManager;
import io.foojay.api.pkg.Distro;
import io.foojay.api.pkg.MajorVersion;
import io.foojay.api.pkg.Pkg;
import io.foojay.api.util.Constants;
import io.foojay.api.util.GithubTokenPool;
import io.foojay.api.util.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static eu.hansolo.jdktools.ArchiveType.SRC_TAR;
import static eu.hansolo.jdktools.ArchiveType.getFromFileName;
import static eu.hansolo.jdktools.OperatingSystem.LINUX;
import static eu.hansolo.jdktools.OperatingSystem.MACOS;
import static eu.hansolo.jdktools.OperatingSystem.WINDOWS;
import static eu.hansolo.jdktools.PackageType.JDK;
import static eu.hansolo.jdktools.PackageType.JRE;


public class GraalVM implements Distribution {
    private static final Logger  LOGGER                        = LoggerFactory.getLogger(GraalVM.class);
    private static final String  PACKAGE_URL                   = "https://download.oracle.com/graalvm/";
    private static final Pattern FILENAME_PATTERN              = Pattern.compile(new StringBuilder().append("^(graalvm-jdk-").append(")(.*)(_bin)(\\.tar\\.gz|\\.zip)$").toString());
    private static final Matcher FILENAME_MATCHER              = FILENAME_PATTERN.matcher("");
    private static final String  EA_BUILDS_URL                 = "https://raw.githubusercontent.com/graalvm/oracle-graalvm-ea-builds/main/versions/";


    // URL parameters
    private static final String        ARCHITECTURE_PARAM      = "";
    private static final String        OPERATING_SYSTEM_PARAM  = "";
    private static final String        ARCHIVE_TYPE_PARAM      = "";
    private static final String        PACKAGE_TYPE_PARAM      = "";
    private static final String        RELEASE_STATUS_PARAM    = "";
    private static final String        SUPPORT_TERM_PARAM      = "";
    private static final String        BITNESS_PARAM           = "";

    private static final HashAlgorithm HASH_ALGORITHM          = HashAlgorithm.NONE;
    private static final String        HASH_URI                = "";
    private static final SignatureType SIGNATURE_TYPE          = SignatureType.NONE;
    private static final HashAlgorithm SIGNATURE_ALGORITHM     = HashAlgorithm.NONE;
    private static final String        SIGNATURE_URI           = "";
    private static final String        OFFICIAL_URI            = "https://www.graalvm.org/";


    public GraalVM() {
    }


    @Override public Distro getDistro() { return Distro.GRAALVM; }

    @Override public String getName() { return getDistro().getUiString(); }

    @Override public String getPkgUrl() { return PACKAGE_URL; }

    @Override public String getArchitectureParam() { return ARCHITECTURE_PARAM; }

    @Override public String getOperatingSystemParam() { return OPERATING_SYSTEM_PARAM; }

    @Override public String getArchiveTypeParam() { return ARCHIVE_TYPE_PARAM; }

    @Override public String getPackageTypeParam() { return PACKAGE_TYPE_PARAM; }

    @Override public String getReleaseStatusParam() { return RELEASE_STATUS_PARAM; }

    @Override public String getTermOfSupportParam() { return SUPPORT_TERM_PARAM; }

    @Override public String getBitnessParam() { return BITNESS_PARAM; }

    @Override public HashAlgorithm getHashAlgorithm() { return HASH_ALGORITHM; }

    @Override public String getHashUri() { return HASH_URI; }

    @Override public SignatureType getSignatureType() { return SIGNATURE_TYPE; }

    @Override public HashAlgorithm getSignatureAlgorithm() { return SIGNATURE_ALGORITHM; }

    @Override public String getSignatureUri() { return SIGNATURE_URI; }

    @Override public String getOfficialUri() { return OFFICIAL_URI; }

    @Override public List<String> getSynonyms() {
        return List.of("graalvm", "GRAALVM", "GraalVM");
    }

    @Override public List<Semver> getVersions() {
        return CacheManager.INSTANCE.pkgCache.getPkgs()
                                             .stream()
                                             .filter(pkg -> getDistro().get().equals(pkg.getDistribution()))
                                             .map(pkg -> pkg.getSemver())
                                             .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Semver::toString)))).stream()
                                             .sorted(Comparator.comparing(Semver::getVersionNumber).reversed())
                                             .collect(Collectors.toList());
    }

    @Override public String getUrlForAvailablePkgs(final VersionNumber versionNumber,
                                                   final boolean latest, final OperatingSystem operatingSystem,
                                                   final Architecture architecture, final Bitness bitness, final ArchiveType archiveType, final PackageType packageType,
                                                   final Boolean javafxBundled, final ReleaseStatus releaseStatus, final TermOfSupport termOfSupport) {
        LOGGER.debug("Query string for {}: {}", this.getName(), PACKAGE_URL);
        return PACKAGE_URL;
    }

    @Override public List<Pkg> getPkgFromJson(final JsonObject jsonObj, final VersionNumber versionNumber, final boolean latest, final OperatingSystem operatingSystem,
                                              final Architecture architecture, final Bitness bitness, final ArchiveType archiveType, final PackageType packageType,
                                              final Boolean javafxBundled, final ReleaseStatus releaseStatus, final TermOfSupport termOfSupport, final boolean onlyNewPkgs) {
        List<Pkg> pkgs = new ArrayList<>();

        VersionNumber vNumber = null;
        String tag = jsonObj.get("tag_name").getAsString();
        if (tag.contains("jdk-")) {
            tag = tag.substring(tag.lastIndexOf("jdk-")).replace("jdk-", "");
            vNumber = VersionNumber.fromText(tag);
        } else if (tag.contains("-dev")) {
            int build = Integer.parseInt(tag.substring(tag.lastIndexOf("_") + 1));
            tag = tag.substring(0, tag.indexOf("-"));
            vNumber = VersionNumber.fromText(tag);
            vNumber.setBuild(build);
        }
        int featureVersion = vNumber.getFeature().getAsInt();

        boolean prerelease = false;
        if (jsonObj.has("prerelease")) {
            prerelease = jsonObj.get("prerelease").getAsBoolean();
        }
        if (prerelease && ReleaseStatus.EA != releaseStatus) { return pkgs; }

        JsonArray assets = jsonObj.getAsJsonArray("assets");
        for (JsonElement element : assets) {
            JsonObject assetJsonObj = element.getAsJsonObject();
            String     filename     = assetJsonObj.get("name").getAsString();
            if (filename.endsWith(Constants.FILE_ENDING_TXT) || filename.endsWith(Constants.FILE_ENDING_JAR) ||
                filename.endsWith(Constants.FILE_ENDING_SHA1) || filename.endsWith(Constants.FILE_ENDING_SHA256)) { continue; }

            FILENAME_MATCHER.reset(filename);
            if (!FILENAME_MATCHER.matches()) { continue; }

            String filenameWithoutPreset = filename.replaceFirst("graalvm-community-jdk-", "").replaceAll("(\\.tar\\.gz|\\.zip)", "").replaceAll("_bin", "");
            String strippedFilename = filenameWithoutPreset.substring(filenameWithoutPreset.indexOf("_"));

            String downloadLink = assetJsonObj.get("browser_download_url").getAsString();

            if (onlyNewPkgs) {
                if (CacheManager.INSTANCE.pkgCache.getPkgs().stream().filter(p -> p.getFilename().equals(filename)).filter(p -> p.getDirectDownloadUri().equals(downloadLink)).count() > 0) { continue; }
            }

            Pkg pkg = new Pkg();

            pkg.setDistribution(getDistro().get());
            pkg.setFileName(filename);
            pkg.setDirectDownloadUri(downloadLink);

            ArchiveType ext = getFromFileName(filename);
            if (SRC_TAR == ext || (ArchiveType.NONE != archiveType && ext != archiveType)) { continue; }
            pkg.setArchiveType(ext);

            Architecture arch = Constants.ARCHITECTURE_LOOKUP.entrySet().stream()
                                                             .filter(entry -> strippedFilename.contains(entry.getKey()))
                                                             .findFirst()
                                                             .map(Entry::getValue)
                                                             .orElse(Architecture.NONE);
            if (Architecture.NONE == arch) {
                LOGGER.debug("Architecture not found in GraalVM" + vNumber.toString(OutputFormat.REDUCED_COMPRESSED, true, true) + " for filename: {}", filename);
                continue;
            }

            pkg.setArchitecture(arch);
            pkg.setBitness(arch.getBitness());

            pkg.setVersionNumber(vNumber);
            pkg.setJavaVersion(vNumber);
            pkg.setDistributionVersion(vNumber);
            pkg.setJdkVersion(new MajorVersion(featureVersion));

            TermOfSupport supTerm = Helper.getTermOfSupport(featureVersion);
            supTerm = TermOfSupport.MTS == supTerm ? TermOfSupport.STS : supTerm;

            pkg.setTermOfSupport(supTerm);

            pkg.setPackageType(JDK);

            pkg.setReleaseStatus(ReleaseStatus.NONE == releaseStatus ? ReleaseStatus.GA : releaseStatus);

            OperatingSystem os = Constants.OPERATING_SYSTEM_LOOKUP.entrySet().stream()
                                                                  .filter(entry -> strippedFilename.contains(entry.getKey()))
                                                                  .findFirst()
                                                                  .map(Entry::getValue)
                                                                  .orElse(OperatingSystem.NONE);

            if (OperatingSystem.NONE == os) {
                switch (pkg.getArchiveType()) {
                    case DEB:
                    case RPM:
                    case TAR_GZ:
                        os = LINUX;
                        break;
                    case MSI:
                    case ZIP:
                        os = WINDOWS;
                        break;
                    case DMG:
                    case PKG:
                        os = MACOS;
                        break;
                }
            }
            if (OperatingSystem.NONE == os) {
                LOGGER.debug("Operating System not found in GraalVM" + vNumber.toString(OutputFormat.REDUCED_COMPRESSED, true, true) + " for filename: {}", filename);
                continue;
            }
            pkg.setOperatingSystem(os);

            pkg.setFreeUseInProduction(Boolean.TRUE);

            pkg.setSize(Helper.getFileSize(downloadLink));

            pkgs.add(pkg);
        }

        // Fetch checksums
        for (JsonElement element : assets) {
            JsonObject assetJsonObj = element.getAsJsonObject();
            String     filename     = assetJsonObj.get("name").getAsString();

            if (null == filename || filename.isEmpty() || !filename.endsWith(Constants.FILE_ENDING_SHA256)) { continue; }
            String nameToMatch;
            if (filename.endsWith(Constants.FILE_ENDING_SHA256)) {
                nameToMatch = filename.replaceAll("." + Constants.FILE_ENDING_SHA256, "");
            } else {
                continue;
            }

            final String  downloadLink = assetJsonObj.get("browser_download_url").getAsString();
            Optional<Pkg> optPkg       = pkgs.stream().filter(pkg -> pkg.getFilename().contains(nameToMatch)).findFirst();
            if (optPkg.isPresent()) {
                Pkg pkg = optPkg.get();
                pkg.setChecksumUri(downloadLink);
                pkg.setChecksumType(HashAlgorithm.SHA256);
            }
        }

        return pkgs;
    }

    public List<Pkg> getAllPkgs(boolean includingEA, final boolean onlyNewPkgs) {
        List<Pkg> pkgs = new ArrayList<>();
        pkgs.addAll(getAllPkgs(PACKAGE_URL, ReleaseStatus.GA, onlyNewPkgs));
        if (includingEA) { pkgs.addAll(getAllPkgs(PACKAGE_URL, ReleaseStatus.EA, onlyNewPkgs)); }
        return pkgs;
    }
    private List<Pkg> getAllPkgs(final String query, final ReleaseStatus releaseStatus, final boolean onlyNewPkgs) {
        if (query.isEmpty()) { return List.of(); }
        try {
            List<Pkg>           pkgs      = new LinkedList<>();
            List<Pkg>           pkgsFound = new ArrayList<>();
            Map<String, String> headers   = new HashMap<>();
            if (query.contains("api.github.com")) {
                headers.put("accept", "application/vnd.github.v3+json");
                headers.put("authorization", GithubTokenPool.INSTANCE.next());
            }
            HttpResponse<String> response = Helper.get(query, headers);
            if (null == response) {
                LOGGER.debug("Response {} returned null.", getDistro().getApiString());
            } else {
                if (response.statusCode() == 200) {
                    String      body    = response.body();
                    Gson        gson    = new Gson();
                    JsonElement element = gson.fromJson(body, JsonElement.class);
                    if (element instanceof JsonArray) {
                        JsonArray jsonArray = element.getAsJsonArray();
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JsonObject pkgJsonObj = jsonArray.get(i).getAsJsonObject();
                            List<Pkg> pkgsInDistribution = getPkgFromJson(pkgJsonObj, null,false, OperatingSystem.NONE, Architecture.NONE, Bitness.NONE, ArchiveType.NONE, PackageType.NONE, null, releaseStatus, TermOfSupport.NONE, onlyNewPkgs);
                            pkgsFound.addAll(pkgsInDistribution);
                        }
                    } else if (element instanceof JsonObject) {
                        JsonObject pkgJsonObj = element.getAsJsonObject();
                        List<Pkg> pkgsInDistribution = getPkgFromJson(pkgJsonObj, null,false, OperatingSystem.NONE, Architecture.NONE, Bitness.NONE, ArchiveType.NONE, PackageType.NONE, null, releaseStatus, TermOfSupport.NONE, onlyNewPkgs);
                        pkgsFound.addAll(pkgsInDistribution);
                    }
                } else {
                    // Problem with url request
                    LOGGER.debug("Error getting packages for {}, calling {}.Response ({}) {} ", getName(), query, response.statusCode(), response.body());
                    return pkgs;
                }
            }

            pkgs.addAll(pkgsFound);
            HashSet<Pkg> unique = new HashSet<>(pkgs);
            pkgs = new LinkedList<>(unique);

            return pkgs;
        } catch (Exception e) {
            LOGGER.debug("Error get packages for {} calling {}. {}", getName(), query, e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Pkg> getAllPkgsFrom17AndAbove(final boolean includingEA, final boolean onlyNewPkgs) {
        final List<Pkg>                                                  pkgs             = new ArrayList<>();
        final String                                                     baseUrl          = "https://download.oracle.com/graalvm/";
        final List<OperatingSystem>                                      operatingSystems = List.of(OperatingSystem.LINUX, OperatingSystem.MACOS, OperatingSystem.WINDOWS);
        final Map<OperatingSystem, Map<Architecture, List<ArchiveType>>> archiveTypeMap   = Map.of(OperatingSystem.LINUX, Map.of(Architecture.AARCH64, List.of(ArchiveType.TAR_GZ),
                                                                                                                                 Architecture.X64, List.of(ArchiveType.TAR_GZ)),
                                                                                                   OperatingSystem.MACOS, Map.of(Architecture.AARCH64, List.of(ArchiveType.TAR_GZ),
                                                                                                                                 Architecture.X64, List.of(ArchiveType.TAR_GZ)),
                                                                                                   OperatingSystem.WINDOWS, Map.of(Architecture.X64, List.of(ArchiveType.ZIP)));

        CacheManager.INSTANCE.getMajorVersions()
                             .stream()
                             .filter(majorVersion -> majorVersion.getAsInt() >= 17)
                             .filter(majorVersion -> majorVersion.getAsInt() != 19)
                             .forEach(majorVersion -> {
                                 final List<Semver> versions = majorVersion.getVersions(BuildScope.BUILD_OF_OPEN_JDK).stream().filter(semver -> semver.getFeature() >= 17).collect(Collectors.toList());
                                 versions.stream().map(semver -> new VersionNumber(semver.getFeature(), semver.getInterim(), semver.getUpdate(), semver.getPatch())).collect(Collectors.toSet()).forEach(version -> {
                                     final int           featureVersion = version.getFeature().getAsInt();
                                     final int           update         = version.getUpdate().getAsInt();
                                     final VersionNumber versionNumber  = new VersionNumber(featureVersion, 0, update, 0);
                                     StringBuilder uriBuilder = new StringBuilder(baseUrl).append(featureVersion).append("/archive/graalvm-jdk-").append(featureVersion);
                                     if (0 == update) {
                                         uriBuilder.append("_");
                                     } else {
                                         uriBuilder.append(".0.").append(update).append("_");
                                     }
                                     int length1 = uriBuilder.length();
                                     operatingSystems.forEach(operatingSystem -> {
                                         uriBuilder.setLength(length1);
                                         uriBuilder.append(operatingSystem.getApiString()).append("-");
                                         int length2 = uriBuilder.length();
                                         archiveTypeMap.get(operatingSystem).entrySet().forEach(entry -> {
                                             uriBuilder.setLength(length2);
                                             Architecture      architecture = entry.getKey();
                                             List<ArchiveType> archiveTypes = entry.getValue();
                                             int length3 = uriBuilder.length();
                                             archiveTypes.forEach(archiveType -> {
                                                 uriBuilder.setLength(length3);
                                                 uriBuilder.append(architecture.getApiString()).append("_").append("bin").append(".").append(archiveType.getApiString());
                                                 final String fileDownloadUri = uriBuilder.toString();
                                                 final String filename        = fileDownloadUri.substring(fileDownloadUri.lastIndexOf("/") + 1);
                                                 final String checksumUri     = fileDownloadUri + ".sha256";

                                                 if (onlyNewPkgs) {
                                                     if (CacheManager.INSTANCE.pkgCache.getPkgs().stream().filter(p -> p.getFilename().equals(filename)).filter(p -> p.getDirectDownloadUri().equals(fileDownloadUri)).count() > 0) { return; }
                                                 }

                                                 if (Helper.isUriValid(fileDownloadUri)) {
                                                     // Create pkg
                                                     Pkg pkg = new Pkg();
                                                     pkg.setDistribution(getDistro().get());
                                                     pkg.setVersionNumber(versionNumber);
                                                     pkg.setJavaVersion(versionNumber);
                                                     pkg.setDistributionVersion(versionNumber);
                                                     pkg.setJdkVersion(new MajorVersion(versionNumber.getFeature().getAsInt()));
                                                     pkg.setPackageType(JDK);
                                                     pkg.setArchitecture(architecture);
                                                     pkg.setBitness(architecture.getBitness());
                                                     pkg.setOperatingSystem(operatingSystem);
                                                     pkg.setReleaseStatus(ReleaseStatus.GA);
                                                     pkg.setTermOfSupport(versionNumber.getMajorVersion().getTermOfSupport());
                                                     pkg.setFileName(filename);
                                                     pkg.setArchiveType(archiveType);
                                                     pkg.setJavaFXBundled(false);
                                                     pkg.setDirectlyDownloadable(true);
                                                     pkg.setFreeUseInProduction(Boolean.TRUE);
                                                     pkg.setDirectDownloadUri(fileDownloadUri);
                                                     if (Helper.isUriValid(checksumUri)) {
                                                         pkg.setChecksumUri(checksumUri);
                                                         pkg.setChecksumType(HashAlgorithm.SHA256);
                                                     }
                                                     pkg.setSize(Helper.getFileSize(fileDownloadUri));

                                                     pkgs.add(pkg);
                                                 }
                                             });
                                         });
                                     });
                                 });
                             });

        if (includingEA) {
            pkgs.addAll(getEaBuildsFromGithub());
        }

        return pkgs;
    }

    public List<Pkg> getEaBuildsFromGithub() {
        LOGGER.error("Fetching ea-pkgs from github");
        List<Pkg> pkgsFound = new ArrayList<>();
        CacheManager.INSTANCE.getMajorVersions().stream().filter(majorVersion -> majorVersion.getAsInt() > 21).forEach(majorVersion -> {
            String jsonFilename = majorVersion.getAsInt() + "-ea.json";
            String eaJsonUri    = EA_BUILDS_URL + jsonFilename;
            try {
                HttpResponse<String> response = Helper.get(eaJsonUri);
                if (null != response) {
                    String body = response.body();
                    if (!body.isEmpty()) {
                        Gson        gson    = new Gson();
                        JsonElement element = gson.fromJson(body, JsonElement.class);
                        if (element instanceof JsonArray) {
                            JsonArray jsonArray = element.getAsJsonArray();
                            for (JsonElement jsonVersionElement : jsonArray) {
                                final JsonObject jsonObj         = jsonVersionElement.getAsJsonObject();
                                final String     version         = jsonObj.get("version").getAsString();
                                final boolean    latest          = jsonObj.get("latest").getAsBoolean();
                                final String     downloadBaseUrl = jsonObj.get("download_base_url").getAsString();
                                final JsonArray  files           = jsonObj.get("files").getAsJsonArray();

                                final VersionNumber versionNumber   = VersionNumber.fromText(version);
                                for (JsonElement jsonFileElement : files) {
                                    final JsonObject      fileObj         = jsonFileElement.getAsJsonObject();
                                    final String          filename        = fileObj.get("filename").getAsString();
                                    final String          arch            = fileObj.get("arch").getAsString();
                                    final String          platform        = fileObj.get("platform").getAsString();

                                    final ArchiveType     archiveType     = ArchiveType.getFromFileName(filename);
                                    final Architecture    architecture    = Architecture.fromText(arch);
                                    final OperatingSystem operatingSystem = OperatingSystem.fromText(platform);
                                    final String          fileDownloadUri = downloadBaseUrl + filename;
                                    final PackageType     packageType     = filename.toLowerCase().contains("-jdk") ? JDK : JRE;

                                    Pkg pkg = new Pkg();
                                    pkg.setDistribution(getDistro().get());
                                    pkg.setVersionNumber(versionNumber);
                                    pkg.setJavaVersion(versionNumber);
                                    pkg.setDistributionVersion(versionNumber);
                                    pkg.setJdkVersion(new MajorVersion(versionNumber.getFeature().getAsInt()));
                                    pkg.setPackageType(packageType);
                                    pkg.setArchitecture(architecture);
                                    pkg.setBitness(architecture.getBitness());
                                    pkg.setOperatingSystem(operatingSystem);
                                    pkg.setReleaseStatus(ReleaseStatus.EA);
                                    pkg.setTermOfSupport(versionNumber.getMajorVersion().getTermOfSupport());
                                    pkg.setFileName(filename);
                                    pkg.setArchiveType(archiveType);
                                    pkg.setJavaFXBundled(false);
                                    pkg.setDirectlyDownloadable(true);
                                    pkg.setFreeUseInProduction(Boolean.TRUE);
                                    pkg.setDirectDownloadUri(fileDownloadUri);
                                    pkg.setSize(Helper.getFileSize(fileDownloadUri));
                                    pkg.setLatestBuildAvailable(latest);

                                    pkgsFound.add(pkg);
                                }
                            }
                        }
                    }
                }
                LOGGER.error("Successfully fetched {} ea-pkgs from github", pkgsFound.size());
            } catch (Exception e) {
                LOGGER.error("Error reading {} file from github. {}", jsonFilename, e.getMessage());
            }
        });
        return pkgsFound;
    }
}
