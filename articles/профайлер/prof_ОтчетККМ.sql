------------------------------  ОТЧЕТ ККМ
-- данные журнала
exec _1sp__1SJOURN_ByIDDOC '  CPVJЦБ '
-- шапка
exec sp_executesql N'Select * from DH802(NOLOCK) where IDDOC=@P1',N'@P1 varchar(9)','  CPVJЦБ '
-- табличная часть
exec sp_executesql N'Select * from DT802(NOLOCK) where IDDOC=@P1 order by IDDOC, LINENO_',N'@P1 varchar(9)','  CPVJЦБ '
-- количество изменений документа
select VERSTAMP from _1SJOURN(NOLOCK) where ROW_ID=51345
-- Справочник Фирмы
exec _1sp_SC321_ByID '     1   '
-- Справочник Склады 
exec _1sp_SC288_ByID '     9ЦБ '
--Справочник Скидки
exec _1sp_SC280_ByID '     1   '
--Справочник Контрагенты
exec _1sp_SC130_ByID '     4   '
--Справочник Договоры
exec _1sp_SC89_ByID '     3   '
-- константа дата запрета редактирования документов
exec sp_executesql N'Select * from _1SCONST(NOLOCK) where ID=@P1 and OBJID=@P2 and DATE<=@P3 order by ID DESC, OBJID DESC, DATE DESC, TIME DESC, DOCID DESC',N'@P1 int,@P2 varchar(9),@P3 datetime',16,'     0   ','1753-01-01 00:00:00'
--Справочник ТипыЦен 
exec _1sp_SC301_ByID '     0   '
--Справочник Валюты
exec _1sp_SC57_ByID '     1   '
--Справочник ФизЛица
exec _1sp_SC313_ByID '     2   '
--Справочник Кассы  
exec _1sp_SC106_ByID '     4ЦБ '
--Справочник Партии
exec _1sp_SC163_ByID '     0   '
--Справочник Пользователи
exec _1sp_SC201_ByID '     FЦБ '
--Справочник Номенклатура
exec _1sp_SC148_ByID '     0   '
go
exec _1sp_SC148_ByID '   26Y   '