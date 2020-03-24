package com.guohao.anything.DatabaseTest;


import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.UUID;

@Table(database = DBFlowDatabase.class)
public class DBFlowDataTable {

    @PrimaryKey // at least one primary key required
            UUID id;

    @Column
    String name;

    @Column
    int age;
}