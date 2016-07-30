drop table if exists balance;

drop table if exists email_task;

drop table if exists employee;

drop table if exists employee_wechat;

drop table if exists `leave`;

drop table if exists `logs`;

drop table if exists manager;

drop table if exists processes;

drop table if exists processes_binding;

drop table if exists processes_node;

drop table if exists processes_result;

/*==============================================================*/
/* Table: balance                                               */
/*==============================================================*/
create table balance
(
   id                   int not null auto_increment,
   empl_id              int,
   empl_no              varchar(36),
   empl_name            varchar(12),
   join_date            datetime,
   after_work           double,
   total_work           double,
   last_year_rest_al    double,
   last_year_rest_lil   double,
   curr_year_al_qua     double,
   curr_end_al          double,
   curr_end_lil         double,
   curr_year_apply_al   double,
   curr_year_apply_lil  double,
   curr_year_add_al     double,
   curr_year_add_lil    double,
   curr_rest_al         double,
   curr_rest_lil        double,
   primary key (id)
);

/*==============================================================*/
/* Table: email_task                                            */
/*==============================================================*/
create table email_task
(
   id                   int not null auto_increment,
   processes_node_id    int,
   leave_id             int,
   execute_date         datetime,
   created_date         datetime,
   status               int comment '{0: 执行中, 1: 已执行}',
   primary key (id)
);

/*==============================================================*/
/* Table: employee                                              */
/*==============================================================*/
create table employee
(
   id                   int not null auto_increment,
   manager_id           int,
   employ_no            varchar(32),
   name                 varchar(12),
   password             varchar(36),
   dept                 varchar(128),
   desig                varchar(128),
   email                varchar(64),
   status               int,
   gone                 int,
   primary key (id)
);

/*==============================================================*/
/* Table: employee_wechat                                       */
/*==============================================================*/
create table employee_wechat
(
   id                   int not null auto_increment,
   user_id              varchar(128),
   empl_id              int,
   primary key (id)
);

/*==============================================================*/
/* Table: "leave"                                               */
/*==============================================================*/
create table `leave`
(
   id                   int not null auto_increment,
   employ_id            int,
   employ_no            varchar(32),
   processes_id         int,
   apply_man_id         int,
   apply_man            varchar(16),
   apply_dept           varchar(16),
   designation          varchar(16),
   type                 varchar(36),
   reason               varchar(2048),
   start_date           datetime,
   end_date             datetime,
   apply_days           double,
   ytd                  double,
   mtd                  double,
   scrip                varchar(1024),
   state                int,
   created_date         datetime,
   primary key (id)
);

/*==============================================================*/
/* Table: logs                                                  */
/*==============================================================*/
create table `logs`
(
   id                   int not null auto_increment,
   man                  varchar(16),
   man_id               int,
   type                 varchar(36),
   from_id              int,
   content              varbinary(1024),
   created_date         datetime,
   primary key (id)
);

/*==============================================================*/
/* Table: manager                                               */
/*==============================================================*/
create table manager
(
   id                   int not null auto_increment,
   account              varchar(24),
   name                 varchar(8),
   password             varchar(36),
   super                int(1) comment '{0: 否, 1: 是}',
   hr                   int comment '{0: 否, 1:是}',
   email                varchar(64),
   status               int,
   primary key (id)
);

/*==============================================================*/
/* Table: processes                                             */
/*==============================================================*/
create table processes
(
   id                   int not null auto_increment,
   name                 varchar(256),
   created_date         datetime,
   status               int,
   primary key (id)
);

/*==============================================================*/
/* Table: processes_binding                                     */
/*==============================================================*/
create table processes_binding
(
   id                   int not null auto_increment,
   man_id               int,
   processes_id         int,
   status               int,
   primary key (id)
);

/*==============================================================*/
/* Table: processes_node                                        */
/*==============================================================*/
create table processes_node
(
   id                   int not null auto_increment,
   processes_id         int,
   manager_id           int,
   manager_name         varchar(256),
   prev_node_id         int,
   next_node_id         int,
   first                int,
   last                 int,
   primary key (id)
);

/*==============================================================*/
/* Table: processes_result                                      */
/*==============================================================*/
create table processes_result
(
   id                   int not null auto_increment,
   leave_id             int,
   processes_node_id    int,
   manager_id           int,
   manager_name         varchar(256),
   state                int comment '{0:在途, 1:待处理, 2:同意, 3: 否决, 4: 终止}',
   suggestion           varchar(2048),
   reply_date           datetime,
   created_date         datetime,
   primary key (id)
);