
grant all on GeyserM2M.timestamps to 'm2mdatabasena'@'localhost' identified by 'ewhM2Mnscl';

grant all on GeyserM2M.* to 'intelligeyser'@'%' identified by 'ewhM2Mnscl';

-------------------------------------------------------------------------------------------
select * from user;

select * from geyser;

select server_stamp,client_stamp,t1,t2,t3,t4 from timestamps where geyser_id=112;
select * from timestamps;


//--------------------------------------------SIMULATION1----------------------------------------------------------------------------------------------------------------------------
insert into user(username,      email,                      password, name,     surname,  cell)
values(          'simulation1', 'andrewhcloete@gmail.com',  'none',   'Andrew', 'Cloete', '0826442538');

insert into geyser(geyser_id, user,          location,   wifiSSID, wifiPSK, modem_cell, spark_id, board_num, IMEI, resetcount, geyser_installation_date, controller_installation_date)
values(            1,         'simulation1', 'Virtual',  'NA',     'NA',    'NA',       'NA',     'NA',      'NA', 0,          '1970-01-01',             '2015-07-25');
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

//--------------------------------------------Raspberry PI (always on, MODEM)-----------------------------------------------------------------------------------------------------------
insert into user(username,      email,                      password, name,     surname,  cell)
values(          'raspi1',      'andrewhcloete@gmail.com',  'none',   'Andrew', 'Cloete', '0826442538');

insert into geyser(geyser_id, user,          location,                 wifiSSID, wifiPSK, modem_cell, spark_id, board_num, IMEI,      resetcount, geyser_installation_date, controller_installation_date)
values(            2,         'raspi1',      'MobileIntelligenceLab',  'NA',     'NA',    'Unknown',  'NA',     'NA',      'Unknown', 0,          '1970-01-01',             '2015-07-25');
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

//--------------------------------------------EC2 (always on, WAN)------------------------------------------------------------------------------------------------------------------
insert into user(username,      email,                      password, name,     surname,  cell)
values(          'ec2sim1',     'andrewhcloete@gmail.com',  'none',   'Andrew', 'Cloete', '0826442538');

insert into geyser(geyser_id, user,          location,             wifiSSID, wifiPSK, modem_cell, spark_id, board_num, IMEI,      resetcount, geyser_installation_date, controller_installation_date)
values(            3,         'ec2sim1',     'EC2-wc',             'NA',     'NA',    'NA',       'NA',     'NA',      'NA',      0,          '1970-01-01',             '2015-07-25');
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

//--------------------------------------------UNIT 110 (LAB)-------------------------------------------------------------------------------------------------------------------------
insert into user(username,  email,                password, name,       surname, cell)
values(         'labunit', '16539710@sun.ac.za', 'none',    'Jonathan', 'Brown', '0715812181');

insert into geyser(geyser_id, user,       location,               wifiSSID, wifiPSK, modem_cell,    spark_id,   board_num, IMEI,              resetcount, geyser_installation_date, controller_installation_date)
values(            110,       'labunit',  'MobileIntelligenceLab', 'NA',     'NA',     '0735635187', 'NA',       '10',       '359983007913688', 0,         '2015-07-01',             '2015-08-01');
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


//--------------------------------------------UNIT 101-------------------------------------------------------------------------------------------------------------------------
insert into user(username,  email,                password, name,       surname, cell)
values(         'u101',     '16539710@sun.ac.za', 'none',   'Jonathan', 'Brown', '0715812181');

insert into geyser(geyser_id, user,       location,   wifiSSID, wifiPSK, modem_cell,    spark_id,   board_num, IMEI,              resetcount, geyser_installation_date, controller_installation_date)
values(            101,       'u101',     'Unknown', 'NA',     'NA',     '0630483782', '--',       '01',       '359983007972098', 0,         '2015-07-01',             '2015-08-01');
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

//--------------------------------------------UNIT 102-------------------------------------------------------------------------------------------------------------------------
insert into user(username,  email,                password, name,       surname, cell)
values(         'u102',     '16539710@sun.ac.za', 'none',   'Jonathan', 'Brown', '0715812181');

insert into geyser(geyser_id, user,       location,   wifiSSID, wifiPSK, modem_cell,    spark_id,   board_num, IMEI,              resetcount, geyser_installation_date, controller_installation_date)
values(            102,       'u102',     'Unknown', 'NA',     'NA',     '0835196977', '--',       '02',       '359983007986528', 0,         '2015-07-01',             '2015-08-01');
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

//--------------------------------------------UNIT 103-------------------------------------------------------------------------------------------------------------------------
insert into user(username,  email,                password, name,       surname, cell)
values(         'u103',     '16539710@sun.ac.za', 'none',   'Jonathan', 'Brown', '0715812181');

insert into geyser(geyser_id, user,       location,   wifiSSID, wifiPSK, modem_cell,    spark_id,   board_num, IMEI,              resetcount, geyser_installation_date, controller_installation_date)
values(            103,       'u103',     'Unknown', 'NA',     'NA',     '0710146839', '--',       '03',       '359983007991338', 0,         '2015-07-01',             '2015-08-01');
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

//--------------------------------------------UNIT 104-------------------------------------------------------------------------------------------------------------------------
insert into user(username,  email,                password, name,       surname, cell)
values(         'u104',     '16539710@sun.ac.za', 'none',   'Jonathan', 'Brown', '0715812181');

insert into geyser(geyser_id, user,       location,   wifiSSID, wifiPSK, modem_cell,    spark_id,   board_num, IMEI,              resetcount, geyser_installation_date, controller_installation_date)
values(            104,       'u104',     'Unknown', 'NA',     'NA',     '0710237821', '--',       '04',       '359983007971918', 0,         '2015-07-01',             '2015-08-01');
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

//--------------------------------------------UNIT 105-------------------------------------------------------------------------------------------------------------------------
insert into user(username,  email,                password, name,       surname, cell)
values(         'u105',     '16539710@sun.ac.za', 'none',   'Jonathan', 'Brown', '0715812181');

insert into geyser(geyser_id, user,       location,   wifiSSID, wifiPSK, modem_cell,    spark_id,   board_num, IMEI,              resetcount, geyser_installation_date, controller_installation_date)
values(            105,       'u105',     'Unknown', 'NA',     'NA',     '0735535177', '--',       '05',       '359983007981115', 0,         '2015-07-01',             '2015-08-01');
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

//--------------------------------------------UNIT 106-------------------------------------------------------------------------------------------------------------------------
insert into user(username,  email,                password, name,       surname, cell)
values(         'u106',     '16539710@sun.ac.za', 'none',   'Jonathan', 'Brown', '0715812181');

insert into geyser(geyser_id, user,       location,   wifiSSID, wifiPSK, modem_cell,    spark_id,   board_num, IMEI,              resetcount, geyser_installation_date, controller_installation_date)
values(            106,       'u106',     'Unknown', 'NA',     'NA',     '0787288787', '--',       '06',       '359983007981180', 0,         '2015-07-01',             '2015-08-01');
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

//--------------------------------------------UNIT 107-------------------------------------------------------------------------------------------------------------------------
insert into user(username,  email,                password, name,       surname, cell)
values(         'u107',     '16539710@sun.ac.za', 'none',   'Jonathan', 'Brown', '0715812181');

insert into geyser(geyser_id, user,       location,   wifiSSID, wifiPSK, modem_cell,    spark_id,   board_num, IMEI,              resetcount, geyser_installation_date, controller_installation_date)
values(            107,       'u107',     'Unknown', 'NA',     'NA',     '0789084530', '--',       '07',       '359983007932035', 0,         '2015-07-01',             '2015-08-01');
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

//--------------------------------------------UNIT 108-------------------------------------------------------------------------------------------------------------------------
insert into user(username,  email,                password, name,       surname, cell)
values(         'u108',     '16539710@sun.ac.za', 'none',   'Jonathan', 'Brown', '0715812181');

insert into geyser(geyser_id, user,       location,   wifiSSID, wifiPSK, modem_cell,    spark_id,   board_num, IMEI,              resetcount, geyser_installation_date, controller_installation_date)
values(            108,       'u108',     'Unknown', 'NA',     'NA',     '0734394018', '--',       '08',       '359983007984424', 0,         '2015-07-01',             '2015-08-01');
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

//--------------------------------------------UNIT 109-------------------------------------------------------------------------------------------------------------------------
insert into user(username,  email,                password, name,       surname, cell)
values(         'u109',     '16539710@sun.ac.za', 'none',   'Jonathan', 'Brown', '0715812181');

insert into geyser(geyser_id, user,       location,   wifiSSID, wifiPSK, modem_cell,    spark_id,   board_num, IMEI,              resetcount, geyser_installation_date, controller_installation_date)
values(            109,       'u109',     'Unknown', 'NA',     'NA',     '0784867869', '--',       '09',       '359983007981263', 0,         '2015-07-01',             '2015-08-01');
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

//--------------------------------------------UNIT 111-------------------------------------------------------------------------------------------------------------------------
insert into user(username,  email,                password, name,       surname, cell)
values(         'u111',     '16539710@sun.ac.za', 'none',   'Jonathan', 'Brown', '0715812181');

insert into geyser(geyser_id, user,       location,   wifiSSID, wifiPSK, modem_cell,    spark_id,   board_num, IMEI,              resetcount, geyser_installation_date, controller_installation_date)
values(            111,       'u111',     'Unknown', 'NA',     'NA',     '0833138993', '--',       '11',       '359983007912748', 0,         '2015-07-01',             '2015-08-01');
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


//--------------------------------------------UNIT 112-------------------------------------------------------------------------------------------------------------------------
insert into user(username,  email,                password, name,       surname, cell)
values(         'u112',     '16539710@sun.ac.za', 'none',   'Jonathan', 'Brown', '0715812181');

insert into geyser(geyser_id, user,       location,   wifiSSID, wifiPSK, modem_cell,    spark_id,   board_num, IMEI,              resetcount, geyser_installation_date, controller_installation_date)
values(            112,       'u112',     'Unknown', 'NA',     'NA',     '--',          '--',       '12',       '--', 0,         '2015-07-01',             '2015-08-01');
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



mysql -h geyserm2m.cuxbzsmchnt1.us-west-2.rds.amazonaws.com -P 3306 -u andrewhcloete -p
mysql -h geyserm2m.cuxbzsmchnt1.us-west-2.rds.amazonaws.com -P 3306 -u geysernip -p

select * from timestamps into outfile '/var/tmp/timestamps.csv';

