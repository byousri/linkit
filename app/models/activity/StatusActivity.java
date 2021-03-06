package models.activity;

import helpers.badge.BadgeComputationContext;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import javax.persistence.Entity;
import javax.persistence.Lob;
import models.Account;
import models.Badge;
import models.Member;
import models.ProviderType;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.IndexColumn;
import play.Logger;
import play.db.jpa.JPA;
import play.i18n.Messages;

/**
 * A status activity : someone ({@link Activity#member} posted a status on an external provider ({@link Activity#session}
 * @author Sryl <cyril.lacote@gmail.com>
 */
@Entity
public class StatusActivity extends Activity {

    @Lob
    public String content;
    public String url;
    
    /** ID of status on external provider */
    @IndexColumn(name="statusId_IDX", nullable=false)
    public String statusId;

    /** Pattern for detecting content dealing with Mix-IT */
    private static final Pattern MIXIT_PATTERN = Pattern.compile(".*\\bmix-?it\\b.*", Pattern.CASE_INSENSITIVE);
    
    public StatusActivity(Member author, Date at, ProviderType provider, String content, String url, String statusId) {
        super(provider, at);
        this.member = author;
        this.provider = provider;
        this.content = StringUtils.substring(content, 0, 4000);
        this.url = url;
        this.statusId = statusId;
    }

    static public void fetchForMember(Long memberId) {

        Member member = Member.findById(memberId);
        for (Account account : member.accounts) {
            
            Logger.info("Fetch timeline for %s on %s", member, account.provider);
            List<StatusActivity> statuses = account.fetchActivities();
            if (!statuses.isEmpty()) {
                // Memorizing most recent id
                Collections.sort(statuses);
                account.lastStatusId = statuses.get(0).statusId;
                account.save();

                account.enhance(statuses);
            }

            for (StatusActivity status : statuses) {
                boolean add = true;
                // Google hack : workaround for lack of "since" parameter in API, returning already fetched statuses.
                if (ProviderType.Google == account.provider) {
                    // We add this status only if we don't have it already in DB
                    long count = StatusActivity.count("provider = ? and statusId = ? ", account.provider, status.statusId);
                    add = (count <= 0l);
                }
                if (add) status.save();
            }
        }
    }
    
    @Override
    public String getMessage(String lang) {
        return Messages.get(getMessageKey(), member, content);
    }

    @Override
    public String getUrl() {
        return url;
    }

    /**
     * @return True if content deals with Mix-IT
     */
    public boolean isAboutMixIT() {
        return MIXIT_PATTERN.matcher(content).matches();
    }
    
    @Override
    protected void computedBadgesForConcernedMembers(BadgeComputationContext context) {
        if (isAboutMixIT()) {
            switch (this.provider) {
                case Twitter :
                    member.addBadge(Badge.Twittos);
                    break;
                case Google :
                    member.addBadge(Badge.Plusoner);
                    break;
            }
            member.save();
        }
    }
}
