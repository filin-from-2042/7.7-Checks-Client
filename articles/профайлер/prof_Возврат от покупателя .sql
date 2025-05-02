------------------------------------------- Возврат от покупателя 
exec _1sp__1SJOURN_ByIDDOC '  CPRGЦБ '
go
-- шапка
exec sp_executesql N'Select * from DH536(NOLOCK) where IDDOC=@P1',N'@P1 varchar(9)','  CPRGЦБ '
-- табличная часть
exec sp_executesql N'Select * from DT536(NOLOCK) where IDDOC=@P1 order by IDDOC, LINENO_',N'@P1 varchar(9)','  CPRGЦБ '
-- количество изменений документа
select VERSTAMP from _1SJOURN(NOLOCK) where ROW_ID=11063
-- Справочник Фирмы
exec _1sp_SC321_ByID '     1   '
--Справочник Договоры
exec _1sp_SC89_ByID '     3   '
--Справочник Контрагенты
exec _1sp_SC130_ByID '     4   '
--Справочник Валюты
exec _1sp_SC57_ByID '     1   '
-- константа дата запрета редактирования документов
exec sp_executesql N'Select * from _1SCONST(NOLOCK) where ID=@P1 and OBJID=@P2 and DATE<=@P3 order by ID DESC, OBJID DESC, DATE DESC, TIME DESC, DOCID DESC',N'@P1 int,@P2 varchar(9),@P3 datetime',16,'     0   ','1753-01-01 00:00:00'

--Справочник Скидки
exec _1sp_SC280_ByID '     1   '
-- Регистр Поставщики  
exec sp_executesql N'Select SUM(SP2108) from RG2113 (NOLOCK) where PERIOD=''20170901'' and SP2104 = @P1  and SP2105 = @P2 GROUP BY PERIOD,SP2104,SP2105',N'@P1 varchar(9),@P2 varchar(9)','     1   ','     3   '
-- Регистр Покупатели
exec sp_executesql N'Select SUM(SP2095) from RG2103 (NOLOCK) where PERIOD=''20170901'' and SP2090 = @P1  and SP2091 = @P2 GROUP BY PERIOD,SP2090,SP2091',N'@P1 varchar(9),@P2 varchar(9)','     1   ','     3   '
-- Справочник Склады 
exec _1sp_SC288_ByID '     4   '
--Справочник ТипыЦен 
exec _1sp_SC301_ByID '     0   '
--Справочник Партии
exec _1sp_SC163_ByID '     0   '
--Справочник Пользователи
exec _1sp_SC201_ByID '     3   '
--Справочник Номенклатура
exec _1sp_SC148_ByID '     0   '