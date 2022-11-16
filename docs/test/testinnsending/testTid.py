from datetime import datetime, timezone, timedelta

visible_after = datetime.now(timezone.utc)
due_before = visible_after + timedelta(days=30)

visible_after_iso_date = visible_after.astimezone().isoformat()
due_before_iso_format = due_before.astimezone().isoformat()
print('visiable', visiable_before_iso_date)
print('due_date', due_date_iso_format)