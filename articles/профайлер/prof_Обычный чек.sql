------------------------------------------- Обычный чек
exec _1sp__1SJOURN_ByIDDOC '  CGQCЦБ '
-- шапка
exec sp_executesql N'Select * from DH1473(NOLOCK) where IDDOC=@P1',N'@P1 varchar(9)','  CGQCЦБ '
-- табличная часть
exec sp_executesql N'Select * from DT1473(NOLOCK) where IDDOC=@P1 order by IDDOC, LINENO_',N'@P1 varchar(9)','  CGQCЦБ '
-- количество изменений документа
select VERSTAMP from _1SJOURN(NOLOCK) where ROW_ID=164608
--Справочник Скидки
exec _1sp_SC280_ByID '     0   '
-- константа дата запрета редактирования документов
exec sp_executesql N'Select * from _1SCONST(NOLOCK) where ID=@P1 and OBJID=@P2 and DATE<=@P3 order by ID DESC, OBJID DESC, DATE DESC, TIME DESC, DOCID DESC',N'@P1 int,@P2 varchar(9),@P3 datetime',16,'     0   ','1753-01-01 00:00:00'
-- Справочник Склады
exec _1sp_SC288_ByID '     4   '
-- Справочник Фирмы
exec _1sp_SC321_ByID '     1   '
--Справочник Валюты
exec _1sp_SC57_ByID '     1   '
--Справочник Номенклатура
exec _1sp_SC148_ByID '     0   '
--Справочник Пользователи
exec _1sp_SC201_ByID '     6   '
go