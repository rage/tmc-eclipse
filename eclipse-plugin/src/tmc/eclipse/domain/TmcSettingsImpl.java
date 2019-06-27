package tmc.eclipse.domain;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import org.apache.http.impl.conn.SystemDefaultRoutePlanner;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import fi.helsinki.cs.tmc.core.configuration.TmcSettings;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.OauthCredentials;
import fi.helsinki.cs.tmc.core.domain.Organization;

public class TmcSettingsImpl implements TmcSettings {
	
	public static final String configDir = "tmc-eclipse";
	private String oauthApplicationId;
	private String oauthSecret;
	private Course currentCourse;
	private String currentOrganizationAsJson;
	private boolean sendDiagnostics;
	private String serverAddress;
	private Path projectRootDir;
	private String oauthToken;
	private String email;
	private int id;
	private String username;

	@Override
	public String clientName() {
		return "eclipse_plugin";
	}

	@Override
	public String clientVersion() {
		return "9000";
	}

	@Override
	public Path getConfigRoot() {
        Path configPath;
        if (isWindows()) {
            String appdata = System.getenv("APPDATA");
            if (appdata == null) {
                configPath = Paths.get(System.getProperty("user.home"));
            } else {
                configPath = Paths.get(appdata);
            }
        } else {
            //Assume we're using Unix (Linux, Mac OS X or *BSD)
            String configEnv = System.getenv("XDG_CONFIG_HOME");
            if (configEnv != null && configEnv.length() > 0) {
                configPath = Paths.get(configEnv);
            } else {
                configPath = Paths.get(System.getProperty("user.home")).resolve(".config");
            }
        }
        return configPath.resolve(configDir);
	}
	
	public static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("windows");
    }

	@Override
	public Optional<Course> getCurrentCourse() {
		return Optional.fromNullable(this.currentCourse);
	}

	@Override
	public Optional<String> getEmail() {
		return Optional.fromNullable(this.email);
	}

	@Override
	public Optional<Integer> getId() {
		return Optional.fromNullable(this.id);
	}

	@Override
	public Locale getLocale() {
		return new Locale("fi_FI");
	}

	@Override
	public Optional<OauthCredentials> getOauthCredentials() {
		OauthCredentials creds = new OauthCredentials(oauthApplicationId, oauthSecret);
		if (creds.getOauthApplicationId() == null || creds.getOauthSecret() == null) {
			return Optional.absent();
		} else {
			return Optional.of(creds);
		}
	}

	@Override
	public Optional<Organization> getOrganization() {
		final String organizationJson = this.currentOrganizationAsJson;
		if (organizationJson == null || organizationJson.isEmpty()) {
			return Optional.absent();
		} else {
			Organization org = new Gson().fromJson(organizationJson, new TypeToken<Organization>(){}.getType());
			return Optional.fromNullable(org);
		}
	}

	@Override
	public Optional<String> getPassword() {
		return Optional.absent();
	}

	@Override
	public boolean getSendDiagnostics() {
		return this.sendDiagnostics;
	}

	@Override
	public String getServerAddress() {
		if (this.serverAddress == null) {
			return "https://tmc.mooc.fi";
		} else {
			return this.serverAddress;
		}
	}

	@Override
	public Path getTmcProjectDirectory() {
		return this.projectRootDir;
	}

	@Override
	public Optional<String> getToken() {
		return Optional.fromNullable(this.oauthToken);
	}

	@Override
	public Optional<String> getUsername() {
		return Optional.fromNullable(this.username);
	}

	@Override
	public String hostProgramName() {
		return "eclipse";
	}

	@Override
	public String hostProgramVersion() {
		return "unknown";
	}

	@Override
	public SystemDefaultRoutePlanner proxy() {
		return null;
	}

	@Override
	public void setCourse(Optional<Course> course) {
		if (course.isPresent()) {
			this.currentCourse = course.get();
		} else {
			this.currentCourse = null;
		}
	}

	@Override
	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public void setOauthCredentials(Optional<OauthCredentials> creds) {
		if (creds.isPresent()) {
			this.oauthApplicationId = null;
			this.oauthSecret = null;
		} else {
			this.oauthApplicationId = creds.get().getOauthApplicationId();
			this.oauthSecret = creds.get().getOauthSecret();
		}
	}

	@Override
	public void setOrganization(Optional<Organization> organization) {
		if (organization.isPresent()) {
            this.currentOrganizationAsJson = new Gson().toJson(organization.get());
        } else {
            this.currentOrganizationAsJson = null;
        }
	}

	@Override
	public void setPassword(Optional<String> password) {
		throw new IllegalArgumentException("Setting passwords is no longer supported!");
	}

	@Override
	public void setServerAddress(String address) {
		this.serverAddress = address;		
	}

	@Override
	public void setToken(Optional<String> token) {
		if (token.isPresent()) {
			this.oauthToken = token.get();
		} else {
			this.oauthToken = null;
		}
	}

	@Override
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public boolean userDataExists() {
		return true;
	}

}
