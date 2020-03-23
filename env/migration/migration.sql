update purchase set attachment_id = null where attachment_id in (select id from attachment where file_path is null);

update shipment set attachment_id = null where attachment_id in (select id from attachment where file_path is null);

delete from attachment where file_path is null;

