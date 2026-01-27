package id.co.lua.pbj.penilaian_karyawan.model.apps;

import com.fasterxml.jackson.annotation.JsonView;
import id.co.lua.pbj.penilaian_karyawan.helpers.View;
import id.co.lua.pbj.penilaian_karyawan.utils.MyDateFormat;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditModel implements Serializable {
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    @JsonView(View.Internal.class)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    @JsonView(View.Internal.class)
    private Date updatedAt;


    @Column(name = "created_by", updatable = false)
    @CreatedBy
    @JsonView(View.Internal.class)
    private String createdBy;

    @Column(name = "updated_by")
    @LastModifiedBy
    @JsonView(View.Internal.class)
    private String updatedBy;

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getCreatedAtIndonesian() {
        return MyDateFormat.getDayAndDateAndTimeIndonesian(this.getCreatedAt());
    }

    public String getCreatedAtIndonesianDay() {
        return MyDateFormat.getDateIndonesian(this.getCreatedAt());
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedAtIndonesian() {
        return MyDateFormat.getDayAndDateAndTimeIndonesian(this.getUpdatedAt());
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
